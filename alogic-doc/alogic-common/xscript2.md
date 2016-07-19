XScript2.0
==========

### 概述
xscript是alogic支持的一种基于XML的脚本语言，用于在JVM内部运行批处理逻辑。

xscript2.0是从xscript1.0的基础上发展而来，增加了服务组装的特性，主要的改进有：
- 调用上下文增加了对象；
- 脚本可以对一份Json文档进行操作;
- 更丰富的内置插件

### 更新历史
- [20160715] duanyy - 新增本文档

### 语法树
xscript采用XML解析器作为词法和语法解析器，在XMLDOM基础上进行语义解析。xscript最为基础的运行逻辑为logiclet，
其中有一种特殊的logiclet为Block，定义了脚本块。Block和一般的Statement构成了xscript的语法树结构。

xscript的语法树结构和XML的树形结构是一致的，这也是采用XML作为基础语法的目的所在。

例如，现有下列的Java代码：
```java
public static void main(){
	System.out.println("Hello world!");
	{
		System.out.println("Hello world again!");
	}
}
```

对应的xscript代码为：
```xml
<script>
	<log msg="Hello world!"/>
	<segment>
		<log msg="Hello world again!"/>
	</segment>
</script>
```

由上面的代码可以看出，xscript的Statement是通过XMLDOM中的Element来定义的，Element的Tag就是Statement的名称。
对于常规编程语言中的代码块，采用Block来定义（上面的script和segment都是继承自Block）。

### 一切都是插件
在xscript中，一切logiclet都是插件，对应着一个Java实现类。xscript包含了一些列的内置插件，包括：
- 使用插件(using)
- 代码块(script,segment,scope)
- 上下文变量(check,checkAndSet,constants,formula,now,select,set,uuid)
- 文档操作(get,location,template)
- 日志(logger,log)
- 工具(descrypt,encrypt)
- 多线程(async,sleep)
- 条件(switch)
- 循环(repeat)
- 异常处理(throw,except,finally)
- 包含(include)

除了这些内置插件，还可用通过using语句来引入自定义插件，例如：
```xml
	<using
		xmlTag="helloworld"
		module="com.alogic.xscript.plugins.Message"
	/>
```
然后，helloworld这个插件可以直接在xscript使用。
```xml
	<helloworld/>
```

### 代码块
xscript代码块相当于java语言中的{}，定义了一个logiclet的组合。例如，下列的Java代码：
```java
	{
		{
		}
		{
			{
			}
		}
		{
		}
	}
```
如果用xscript来表达的话，就是:
```xml
	<script>
		<segment/>
		<segment>
			<segment/>
		</segment>
		<segment/>
	</script>
```

> segment和script并没有明显区别，我们只是在代码上，约定script应该作为语法树的根节点，而处理中间位置的代码块，
> 则用segment。实际上，script是继承自segment，并且没有自己的实现。

segment维护了一个logiclet列表，在执行的时候，依次对该列表中的logiclet进行执行。

> logiclet如何执行取决于Block的实现，例如在异步调用Block（即语句async）中，对logiclet进行多线程同时执行。

和其他编程语言一样，代码块除了能形成良好的编码风格之外，也将影响局部变量的作用域。

> 在xscript1.0中,segment意味着创建了一个新的作用域。而在2.0中，考虑到了服务组装的场景，不再创建一个新的作用域，如有需要，可以使用scope.


### 变量操作
xscript支持变量，但变量只有一种类型，即字符串。

在某些服务性的场合，我们需要对前端传入的参数进行检查，可以使用check和checkAndSet。check用于检查指定的参数是否存在，如果不存在，则抛出异常；
而checkAndSet用于检查参数是否存在，如果不存在，则设置一个缺省值。

可以根据规则计算出一个变量，然后设置到上下文之中，例如：
- constants(指定一个常量进行设置)
- now(获取当前时间进行设置)
- uuid(生成一个随机uuid进行设置)
- formula(通过一个表达式计算出一个值进行设置)
- set(通过变量计算功能进行设置)
- select(通过selector框架进行设置)

### 变量的作用域
每个设置的变量是有作用域的，作用域主要通过scope代码段来控制。下列样例显示了scope和segment的区别：

案例1:
```xml
	<script>
		<scope>
			<constants id="name" value="alogic"/>
			<log msg="the variable is '${name}'"/>
			<scope>
				<log msg="the variable is '${name}'"/>
			</scope>
		</scope>
		<log msg="the variable is '${name}'"/>
	</script>
```
在上面的代码中，首先在scope中，定义了一个id为name的变量，取值为alogic；接着，在当前代码块中，通过log语句打印出该变量的值；
接着在当前代码块的子代码块(child)中，通过Log语句打印出该变量的值；最后在代码块之外，再次通过log语句打印该变量值。

可以看到结果如下；
```
the variable is 'alogic'
the variable is 'alogic'
the variable is ''
```
也就是说在代码块之外是取不到这个变量的，而在当前代码块和子代码块中均可取到这个变量。

案例2:
```xml
	<script>
		<segment>
			<constants id="name" value="alogic"/>
			<log msg="the variable is '${name}'"/>
			<segment>
				<log msg="the variable is '${name}'"/>
			</segment>
		</segment>
		<log msg="the variable is '${name}'"/>
	</script>
```

上面的代码和案例1的区别在于使用segment替代了scope，可以看到结果如下；

```
the variable is 'alogic'
the variable is 'alogic'
the variable is 'alogic'
```
也就是说segment并没有创建新的作用域。

### 文档操作
前面已经提到，xscript2.0增加了服务组装的使用场景，具体就体现在脚本能够对一份JSON文档进行操作。

文档操作相关的语句包括：
- get(获取指定的变量，并将变量值输出到当前文档节点)
- template(解析指定的JSON文档，并输出到当前文档节点)
- location(通过jsonPath定位到文档指定的节点，从而切换当前节点)

### 条件语句
作为一门语言，条件语句是少不了的啦。xscript中的条件语句是switch。

switch类似于java中的switch句式，用法如下：
```xml
	<switch value="${caseValue}">
		<foo1 case="case1"/>
		<foo2 case="case2"/>
		<foo3 case="case3"/>
		<foo4 case="default"/>
	</switch>
```
switch本身通过value计算出一个值，该值将和子logiclet列表中的case进行比对，如果匹配，则执行对应的logiclet，如果匹配不到，则执行case值为default的logiclet
（如果没有定义default的case，则不做任何事）。

### 循环语句

to be define

### 多线程
前面提到logiclet对其子logiclet列表是按照次序顺序同步执行的。那么问题来了，能否支持多线程，进行异步执行？

这就用到async语句了。

async语句也是一个Block实现，只不过他按照子logiclet列表中logiclet个数分为多个线程，每个logiclet获取一个线程，从而同步执行。

> 这是xscript最令人兴奋的功能，你能想象多线程编程来的这么容易么？


我们看一个例子：
```xml
	<async>
		<foo1/>
		<foo2/>
		<foo3/>
	</async>
```
在例子中foo1,foo2,foo3将并发执行。

多线程同样会带来不确定性，会导致不可预测的结果，在使用的时候应该知道自己在干什么。如果把上面的例子改成多线程，看看会发生什么？
```xml
	<script>
		<async id="segment">
			<var id="name" selector-value="alogic"/>
			<log msg="the variable is '${name}'"/>
			<foo1/>
			<foo2/>
			<foo3/>
		</async>
	</script>
```
可以发现，log语句是依赖var语句存在的（因为log要读取var所设置的变量），假如在多线程环境下，var和log执行的先后是无法确定的，那么就无法保证log语句一定能读取到变量name。这种情况下，建议将var和log放入同一个代码块。
```xml
	<script>
		<async id="segment">
			<segment>
				<var id="name" selector-value="alogic"/>
				<log msg="the variable is '${name}'"/>
			</segment>
			<foo1/>
			<foo2/>
			<foo3/>
		</async>
	</script>
```

### 异常处理
异常处理是任何一门语言所应该具备的。xscript不支持通过检测logiclet的执行返回值来检测异常，xscript支持各logiclet实现类所抛出的BaseException异常。

和java语言类似，xscript通过except和finally来处理异常。任何一个block里面都能够注册一个finally处理块和多个except处理块。

except类似于java中的catch语句，用法如下：
```xml
	<segment>
		<except id="code1">
			<foo1/>
		</except>
		<except id="code2">
			<foo2/>
		</except>
		<except>
			<foo3/>
		</except>
	</segment>
```
代码块中允许定义一个或多个except处理块，每个处理块需要定义id以便和其他处理块相区别，这个id就是本代码块中其他代码所抛出的异常代码
（参见com.anysfot.util.BaseException的实现），当没有配置id的时候，采用一个缺省的特殊ID，即“except"。

处理逻辑如下：当本代码块或者子代码块抛出BaseException异常的时候，获取该异常的code，然后以该code搜索所注册的except处理块，如果找到，
则执行该处理块，如果没有找到，则查找id为except的缺省处理块，如果找到，则执行该缺省处理块，如果还是没有找到，则向父代码块抛出该异常。

> 如果注册了多个同一id的except处理块，则以最后一个为准，其他的全部被忽略。


代码块中允许定义唯一一个finally处理块。finally的语义是：在改代码块所有代码执行完毕之后，无论是否发生异常，都会执行。
其实finally处理块的内部实现也是采用except，只不过是一个id为finally的特殊的except处理块。

> 我们约定finally和except作为xscript的保留字，不得作为except的id来配置，如：
> ```xml
> <except id="finally"/>
> <except id="except"/>
> ```
> 其实等同于：
> ```xml
> <finally/>
> <except/>
> ```
> 第一种写法是不建议的。
>


除了在Statement的实现类中抛出BaseException之外，xscript提供了throw语句让你主动在脚本中抛出异常。

throw的用法如下：
```xml
	<throw id="<id>" msg="<msg>"/>
```
throw允许你指定一个异常ID和异常的信息，组合成BaseException抛出。

### 包含
include用于包含另外一个xscript脚本,用法如下：
```
	<include src="<url>"/>
```

### 日志处理
脚本中，日志的输出可以通过log语句来进行日志的输出，例如：
```xml
	<log msg="Helloworld" progress="100" level="info" id="activity1"/>
```
上述语句中，msg参数定义了要输出的信息模板（请注意是模板，模板是支持变量的）；progress定义当前处理的进度，它是一个整型值
（从-2至10001,-2代表非进度,-1代表还没开始,10001 代表已经完成,0-10000 代表以10000为基数的百分比），缺省为-2；
level定义了日志的级别，分为三个级别：error,warn,info；id定义了当前活动的id，缺省为语句id(即log)。

这就意味着，你可以在脚本中向当前的日志处理器输出信息。

采用logger语句来定义ScriptLogger，例如:
```xml
	<logger module="com.alogic.xscript.log.Default"/>
```
上面的代码定义了一个缺省实现的日志处理器（实现为com.alogic.xscript.log.Default，采用log4j做了简单实现）

> ScriptLogger和BizLogger类似，采用了一个基于有向树的流处理框架(com.anysoft.stream)，你可以定制ScriptLogger插件来实现日志的处理。


值得注意的是，logger是定义在代码块下面的，也就是说每个代码块都可以定义一个自己的logger，如果代码块没有定义自己的logger，日志信息
会被向父语句节点传递。对于根节点（script语句），如果没有定义logger，则会自动创建一个缺省实现的logger（即com.alogic.xscript.log.Default）。

### 如何使用xscript？
上面已经介绍了这个简单而强大的全新的脚本语言，那么问题来了，如何使用？

alogic提供了Script类(com.alogic.xscript.Script)的几个静态方法的来使用xscript。xscript的使用分为两步：

* **编译**
xscript的编译过程是将一个XML资源或XMLDOM编译为xscript的语法树，编译过程是通过XMLConfigurable接口来进行的：

```java
	public interface XMLConfigurable {
		/**
		 * to read config from xml.
		 * @param e xml document
		 * @param p variables
		 */
		public void configure(Element e,Properties p);
	}	
```

Script提供了三个静态方法来进行编译，可根据具体情况来使用：
```java
	/**
	 * 根据XML创建脚本
	 */
	public static Script create(Element root,Properties p);
	
	/**
	 * 根据XML配置文件的位置创建服务脚本
	 */
	public static Script create(String src,Properties p);
	
	/**
	 * 根据XML配置文件在CLASSPATH中的路径创建服务脚本
	 */
	public static Script create(String bootstrap,String path,Properties p);	
```

方法的返回是一个Script对象，为script语句所对应的对象，代表了一个xscript语法树。该对象可以直接进行执行。

* **执行**
xscript的执行过程是通过上下文环境来执行该logiclet。

```java
	/**
	 * 执行
	 * @param root 结果文档根目录
	 * @param current 结果文档当前目录
	 * @param ctx 服务上下文
	 */
	public void execute(
		Map<String,Object> root,
		Map<String,Object> current,
		LogicletContext ctx,
		ExecuteWatcher watcher);
```

下面是一个执行xscript脚本的Demo.

```java
	public static void main(String[] args) {
		//环境变量准备
		Settings settings = Settings.get();		
		settings.addSettings(new CommandLine(args));		
		settings.addSettings("java:///conf/settings.xml#App",null,Settings.getResourceFactory());

		//编译脚本
		Script script = Script.create("java:///load.xml#App", settings);
		if (script == null){
			System.out.println("Fail to compile the script");
			return;
		}
		
		//准备脚本的上下文
		// -- 文档
		Map<String,Object> root = new HashMap<String,Object>();
		// -- 变量集
		LogicletContext ctx = new LogicletContext(settings);
		
		// 执行脚本
		script.execute(root, root, ctx, new ExecuteWatcher.Quiet());
		
		//输出结果
		JsonProvider provider = JsonProviderFactory.createProvider();
		System.out.println(provider.toJson(root));		
	}
```

### 如何开发xscript插件？
xscript的另外一个令你兴奋功能就是强大的插件机制。你可以定制自己的插件，从而不断的扩充xscript的功能。

开发插件的方法就是实现logiclet接口。
```java
	public interface Logiclet extends Configurable,XMLConfigurable,Reportable{
		
		/**
		 * 执行
		 * @param root 结果文档根目录
		 * @param current 结果文档当前目录
		 * @param ctx 服务上下文
		 */
		public void execute(Map<String,Object> root,Map<String,Object> current,LogicletContext ctx,ExecuteWatcher watcher);
		
		/**
		 * 记录日志
		 * @param logInfo
		 */
		public void log(LogInfo logInfo);
		
		/**
		 * 获取父节点
		 * @return 父节点实例
		 */
		public Logiclet parent();
		
		/**
		 * 是否可执行
		 * @return true|false
		 */
		public boolean isExecutable();
		
		/**
		 * 获取XML语法的tag
		 * @return tag
		 */
		public String getXmlTag();
		
		/**
		 * 根据xmlTag创建logiclet
		 * @param xmlTag xml tag
		 * @param parent 父节点
		 * @return logiclet实例
		 */
		public Logiclet createLogiclet(String xmlTag,Logiclet parent);
		
		/**
		 * 注册Logiclet的实现模块
		 * <p>
		 * 所注册的module在该节点及其子节点有效。
		 * 
		 * @param xmltag xml tag
		 * @param clazz Class实例
		 */
		public void registerModule(String xmltag,Class<?extends Logiclet> clazz);
		
		/**
		 * 注册异常处理模块
		 * @param id 异常code
		 * @param exceptionHandler 异常处理段
		 */
		public void registerExceptionHandler(String id,Logiclet exceptionHandler);	
		
		/**
		 * 注册日志处理器
		 * @param logger 日志处理器
		 */
		public void registerLogger(Handler<LogInfo> logger);
	}
```
但是通常，我们都是基于AbstractLogiclet来定制插件，AbstractLogiclet已经帮你完成了Logiclet接口中令你迷惑的功能，让你专注自己的业务逻辑。

没错，这个时候Helloworld又出现了。

我们要定制一个Helloworld插件，在log4j中输出一些东西。
```java
	public class Helloworld extends AbstractLogiclet {
	
		public Helloworld(String tag, Logiclet p) {
			super(tag, p);
		}
	
		@Override
		public void configure(Properties p){
			// read configuration
		}
		
		@Override
		protected void onExecute(Map<String, Object> root, Map<String, Object> current, LogicletContext ctx,
				ExecuteWatcher watcher) {
			logger.info("java.vm.name=" + PropertiesConstants.getString(ctx,"java.vm.name",""));
			logger.info("java.vm.version=" + PropertiesConstants.getString(ctx,"java.vm.version",""));
			logger.info("java.vm.vendor=" + PropertiesConstants.getString(ctx,"java.vm.vendor",""));
		}
	
	}
```
基于AbstractLogiclet，你只需要完成两件事。第一，实现configure方法来读取自己所需的配置，该方法在编译期间被调用；第二，实现onExecute方法来做自己的事情。

### 面向服务的场景

参见[基于xscript的together](xscript2-together.md)。

### 插件参考

参见[xscript-plugins参考](xscript2-plugins.md)。

### 后记
后面就交给你，请用xscript实现你的业务逻辑吧！
