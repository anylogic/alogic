together开发指引
================

### 什么是Together?

Together是anyLogicBus(v1.1.0)提供的一个服务组装框架，具体两个特征：

- 面向协议，服务组装配置文档即服务协议，所见即所得；
- 并发调度，将服务拆分为更小粒度的logiclet，支持进行异步并发调度

### together的原理

Together试图将服务实现拆分为更小粒度的单元，称之为logiclet，然后采用配置的方式，将logiclet组合起来，形成服务。这也是主流SOA产品视图达到的目的，主流的SOA产品主要通过定义过程来配置组装。而Together另辟蹊径，采取面向协议的配置方法来配置服务，设计协议的同时也完成了服务组装的配置，"所见即所得"。

Together配置文档是一个XML，例如：

```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <logiclet module="Segment" async="true" timeout="5000">
	    <content>
		    <note>这是一个测试模板</note>
		    <note>1.下面是Helloworld的测试</note>
		    <note><logiclet module="Helloworld" welcome="Hello everyone."/></note>
		    <note>2.下面是Simulator的测试</note>
		    <note><logiclet module="Simulator" avg="100"/></note>
	    </content>
    </logiclet>
```
在XML中，嵌入了一些特殊性的节点logiclet（类似于JSP中的TAG）,一个logiclet节点就定义了一个logiclet.

Logiclet被定义为完成某项业务逻辑的服务单元，他所做的事情就是向其配置节点所在的父节点根据业务逻辑需要写出相应的动态内容。当一个服务文档中所有logiclet被执行完之后，这个服务也就完成了，服务输出的协议结构和配置文档的结构完全一致。

如果这些logiclet适合异步并发执行，可以配置为异步执行，达到并发执行的目的。

logiclet是可以嵌套的，支持诸如选择、循环等控制功能，形成一棵logiclet执行树。对于一个服务的执行过程，就是调度logiclet树的过程。

Together框架调度过程如下：
- 初始化阶段，对配置文档进行编译，创建logiclet执行树；
- 执行阶段，结合服务的实时信息（服务消息、上下文等），对logiclet执行树进行执行。

### Logiclet的开发

所有的Logiclet都必须实现{@link com.logicbus.together.Logiclet Logiclet}接口。Logiclet重要的接口包括：
```java
	/**
	 * 编译配置文档
	 * 
	 * @param config 配置文档中本logiclet所对应的节点
	 * @param props 环境变量
	 * @param parent 父节点
	 * @param factory logiclet工厂类
	 * @throws ServantException
	 */
	public void compile(Element config,Properties props,Logiclet parent,LogicletFactory factory);

	/**
	 * 执行
	 * 
	 * @param target 输出的XML节点
	 * @param msg 服务消息
	 * @param ctx 服务调用上下文
	 * @param watcher 执行监视器
	 * @return
	 * @throws ServantException
	 */
	public void excute(Element target,Message msg,Context ctx,ExecuteWatcher watcher);
```

compile用于初始化,对本logiclet及子logiclet进行初始化。excute用于执行，结合目标节点，服务消息，上下文进行计算。

在实际开发中，建议继承{@link com.logicbus.together.AbstractLogiclet AbstractLogiclet}以便利用AbstractLogiclet所提供的公共功能。

AbstractLogiclet所提供的公共功能包括：
- 运行时参数提取框架,从目标节点、服务消息、上下文中提取参数
- 构建父节点链条，父节点链条主要用于服务参数的传递
- 提供创建Logiclet的工厂类

传说中的Helloworld又出现了。想不到还是要用到Helloworld.

下面的任务是向目标节点上附加一个say节点。
```java
    public class Helloworld extends AbstractLogiclet {

	    @Override
	    protected void onCompile(Element config, Properties myProps,LogicletFactory factory)
			throws ServantException {
		    welcome = PropertiesConstants.getString(myProps, "welcome", welcome);
	    }

	    @Override
	    protected void onExecute(Element target, Message msg, Context ctx,ExecuteWatcher watcher)
			throws ServantException {
		    Document doc = target.getOwnerDocument();
		    Element say = doc.createElement("say");
		    say.appendChild(doc.createTextNode(welcome));
		    //在异步执行模式下，在最后提交到target上时，进行同步控制
		    synchronized (target){
			    target.appendChild(say);
		    }
	    }
	   /**
	    * 欢迎语
	    */
	    protected String welcome = "Hello world";
    }
```
在服务配置中，上述的Helloworld可以这样配置：
```xml
    <logiclet module="Helloworld" welcome="Hello everyone."/>
```

#### 异常的处理

每个logiclet可定义ignoreException参数(缺省为false)，如果ignoreException参数为true时，则忽略自身的异常及子logiclet的异常，如果定义为false，将设置自身为异常状态。

父节点通过判断子节点的异常状态来判定异常，相关方法为：
```java
	/**
	 * 获取结果代码
	 * @return
	 */
	public String getCode();

	/**
	 * 获取结果原因
	 * @return
	 */
	public String getReason();
	
	/**
	 * 是否存在错误
	 * @return
	 */
	public boolean hasError();
```

#### 耗时统计
每个logiclet可配置outputDuration参数(缺省为false),如果outputDuration为true，将会把每个logiclet执行时间写出到目标节点的属性中。

### 特色的Logiclet

在Together中,有一些特殊的Logiclet,用于控制和管理，包括：
- Segment
- Repeator
- Selector

#### Segment
Segment是一个logiclet容器，可以容纳一段协议内容及多个子logiclet。主要从事下列的工作：
- 对协议内容的复制，将配置中的XML树形结构复制到服务实例数据中；
- 对子logiclet进行调度，支持同步调度和异步调度

一个服务协议的根目录通常就是Segment类型的logiclet.

##### 协议内容的复制

例如
```xml
    <logiclet id="root" module="Segment">
	    <content>
		    <note>这是一个测试模板</note>
		    <note>1.下面是Helloworld的测试</note>
            <note>2.下面是Simulator的测试</note>
	    </content>
    </logiclet>
```
id为root的Segment负责将其子节点content下面的所有内容复制到服务实例数据(目标节点为root)中，生成结果为:
```xml
    <root id="target">
        <note>这是一个测试模板</note>
		<note>1.下面是Helloworld的测试</note>
        <note>2.下面是Simulator的测试</note>
    </root>
```

##### 同步调度

Segment负责对子logiclet进行调度，可以配置为同步调度(async=false),例如：
```xml
    <logiclet id="root" module="Segment" async="false">
        <content>
            <logiclet id="A"/>
            <logiclet id="B"/>
            <logiclet id="C"/>
            <logiclet id="D"/>
        </content>
    </logiclet>
```
在上面的样例中，调度次序为：
```
root -> A -> B -> C -> D
```

##### 异步调度

Segment同样可以配置为异步调度(async=true),例如：
```xml
    <logiclet id="root" module="Segment" async="true">
        <content>
            <logiclet id="A"/>
            <logiclet id="B"/>
            <logiclet id="C"/>
            <logiclet id="D"/>
        </content>
    </logiclet>
```

在上面的样例中，调度次序为：
```
    root-> A
        -> B
        -> C
        -> D
```

异步调度模式为缺省模式。

在异步模式下，可以设置timeout变量，如果子logiclet在timeout时间内没有完成的话，将会按超时异常处理。

##### 混合调度

在实际操作中，我们当然希望所有的logiclet都能异步调度以提高效率，但必然会有部分logiclet存在先后关系。这个时候，可以采用混合调度的方式。例如：

```xml
    <logiclet id="root" module="Segment" async="true">
        <content>
            <logiclet id="A"/>
            <logiclet id="B"/>
            <logiclet id="C" module="Segment" async="false">
                <content>
                    <logiclet id="E"/>
                    <logiclet id="F"/>
                </content>
            </logiclet>
            <logiclet id="D" module="Segment" async="true">
                <content>
                    <logiclet id="G"/>
                    <logiclet id="H"/>
                </content>            
            </logiclet>
        </content>
    </logiclet>    
```
在上面的案例中，调度次序如下：
```
    root-> A
        -> B
        -> C -> E -> F
        -> D -> G
             -> H
```

#### Repeator

Repeator用于作循环控制，结合目标节点下多个字节点，循环调用子logiclet。例如：
```xml
    <logiclet module="Segment">
        <content>
            <a welcome="hello 1"/> 
            <a welcome="hello 2"/>
            <a welcome="hello 3"/>
            <a welcome="hello 4"/>
            <a welcome="hello 5"/>
            <logiclet module="Repeator" target="a">
                <logiclet module="Helloworld"/> 
            </logiclet>
        </content>
    </logiclet>
```
上述样例的输出结果为：
```xml
    <a welcome="hello 1"><say>hello 1</say></a>
    <a welcome="hello 2"><say>hello 2</say></a>
    <a welcome="hello 3"><say>hello 3</say></a>
    <a welcome="hello 4"><say>hello 4</say></a>
    <a welcome="hello 5"><say>hello 5</say></a>
```

可以看出，Repeator针对目标节点下的"a"路径进行了循环(a是XPath语法，可以基于目标节点查询出节点列表)。

#### Selector

Selector用于做选择控制，结合条件进行公式计算，为公式计算结果找到可以匹配的子logiclet。例如：
```xml
	<staff sex="female">
        <logiclet module="Selector" condition="sex">
            <logiclet option="male" module="Segment">
                <content><note>male</note></content>		            
		    </logiclet>
            <logiclet option="female" module="Segment">
                <content><note>female</note></content>		            
		    </logiclet>
	    </logiclet>
    </staff>
```
上述案例的输出结果为:
```xml
    <note>female</note>
```

可以看出，Selector采用公式"sex",对条件进行了计算，计算结果为"female"，然后找到匹配female的logiclet进行计算。

Selector所用的公式解析器为anyFormula,已经在github上开源，见https://github.com/yyduan/anyFormula

### together on anyLogicbus

Together的设计目的是在anyLogicBus上提供服务组装和并行调度框架，已经在anyLogicBus的Servant规范上开发了服务代理，见{@link com.logicbus.together.service.LogicBusAgent LogicBusAgent}

```java
    public class LogicBusAgent extends Servant {
    
    	@Override
    	public int actionProcess(MessageDoc msgDoc, Context ctx) throws Exception {
    		XMLMessage msg = (XMLMessage) msgDoc.asMessage(XMLMessage.class);
    		
    		String reload = getArgument("reload","false",msgDoc,ctx);
    		
    		if (reload.equals("true")){
    			reloadProtocol();
    		}
    		
    		if (dbSupport){
    			ConnectionPool pool = ConnectionPoolFactory.getPool();
    			Connection conn = pool.getConnection(dsName, 3000);
    			
    			if (conn == null) 
    					throw new ServantException("core.sqlerror","Can not get a db connection : " + dsName);
    			
    			if (transactionSupport){
    				conn.setAutoCommit(false);
    			}else{
    				conn.setAutoCommit(true);
    			}
    			
    			ctx.setConnection(conn);
    			
    			try {
    				Element root = msg.getRoot();
    				if (logiclet != null){
    					logiclet.excute(root, msg, ctx,null);
    					if (logiclet.hasError()){
    						throw new ServantException(logiclet.getCode(),logiclet.getReason());
    					}
    				}
    				if (transactionSupport){
    					conn.commit();
    				}
    			}catch (Exception ex){
    				if (transactionSupport){
    					conn.rollback();
    				}
    				throw ex;
    			}finally{
    				ctx.setConnection(null);
    				SQLTools.close(conn);
    			}
    		}else{
    			Element root = msg.getRoot();
    			if (logiclet != null){
    				logiclet.excute(root, msg, ctx,null);
    				if (logiclet.hasError()){
    					throw new ServantException(logiclet.getCode(),logiclet.getReason());
    				}
    			}
    		}
    		return 0;
    	}
    
    	@Override
    	public void create(ServiceDescription sd) throws ServantException{
    		super.create(sd);
    		
    		Properties props = sd.getProperties();		
    		dbSupport = PropertiesConstants.getBoolean(props, "dbSupport", dbSupport);		
    		dsName = PropertiesConstants.getString(props, "datasource", dsName);
    		transactionSupport = PropertiesConstants.getBoolean(props, "transactionSupport", transactionSupport);	
    		
    		xrcMaster = PropertiesConstants.getString(props, "xrc.master", "${master.home}/servants/" + sd.getPath() + ".xrc");
    		xrcSecondary = PropertiesConstants.getString(props, "xrc.secondary", "${secondary.home}/servants/" + sd.getPath() + ".xrc");
    		
    		reloadProtocol();
    	}	
    	
    	protected void reloadProtocol(){
    		Settings settings = Settings.get();
    		ResourceFactory rm = (ResourceFactory) settings.get("ResourceFactory");
    		if (null == rm){
    			rm = new ResourceFactory();
    		}
    		
    		Document doc = null;
    		InputStream in = null;
    		try {
    			in = rm.load(xrcMaster, xrcSecondary);
    			doc = XmlTools.loadFromInputStream(in);
    			
    			logiclet = Compiler.compile(doc.getDocumentElement(), Settings.get(),null);			
    			if (logiclet == null){
    				logger.error("Can not compile the document,xrc =" + xrcMaster);
    			}
    		} catch (Exception ex){
    			logger.error("Error occurs when load xml file,source=" + xrcMaster , ex);
    		}finally {
    			IOTools.closeStream(in);
    		}
    	}
    	
    	/**
    	 * 协议文档主URI
    	 */
    	protected String xrcMaster = "";
    	
    	/**
    	 * 协议文档备URI
    	 */
    	protected String xrcSecondary = "";
    	
    	/**
    	 * Logiclet根节点
    	 */
    	protected Logiclet logiclet = null;
    	
    	/**
    	 * 是否需要数据库连接,缺省是false
    	 */
    	protected boolean dbSupport = false;
    	
    	/**
    	 * 是否需要事务支持,缺省是false
    	 */
    	protected boolean transactionSupport = false;
    	
    	/**
    	 * 数据库数据源名称,缺省为Default
    	 */
    	protected String dsName = "Default";
    }
```


