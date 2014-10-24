anyLogicBus开发文档
===================

@[开发文档|手册]

### 访问控制器开发

anyLogicBus支持自定义访问控制器。

访问控制器用于控制客户端对服务器访问的权限，定义了anyLogicBus调度框架访问控制的基本行为。调度框架对AccessController的调用如下：
- 首先，调度框架会调用createSessionId()函数创建会话ID;
- 在调用服务之前，调度框架会调用访问控制器的accessStart(String,Path, ServiceDescription, Context)
- 在完成服务之后，会调用访问控制器的accessEnd(String,Path, ServiceDescription, Context)

访问控制器在accessStart(String, ServiceDescription, Context)中通过返回值和框架约定权限控制方式，如果返回值小于0，则表明 本次无权访问；如果返回值大于1，则表明本次访问为高优先级访问；其他则表明本次访问为低优先级访问。

设想一下这样的访问控制需求：
- 如果服务的可见性为public，则允许所有主机访问，优先级为低优先级。
- 如果服务的可见性为protected，则只允许192.168.1.*网段能够访问，优先级为高优先级。

我们需要创建一个实现类，来实现AccessController接口。

```java
    public class MyAccessController implements AccessController {
        public int accessStart(String sessionId,Path serviceId, ServiceDescription servant,Context ctx) {
            String visible = servant.getVisible();
            if (visible.equal("public")){
                // 如果服务可见性为public，优先级为低优先级
                return 0;
            }
            String ip = ctx.getClientIP();
            if (ip.startWith("192.168.1.") && visible.equal("protected")){
                // 如果可见性为protected，且属于192.168.1.*网段，优先级为高优先级
                return 1;
            }
            //其他的，拒绝服务
            return -1;
        }
    }
```
在web.xml或者全局变量中指定AccessController。
```xml
	acm.module = MyAccessController
```

在此案例中，仅仅实现accessStart方法实现了需求。如果涉及到更为复杂的访问控制，需要保存一些会话信息，则需要用到createSessionId()和accessEnd(String,Path, ServiceDescription, Context)，可参见内置的相关实现。

 - {@link com.logicbus.backend.IpAndServiceAccessController IpAndServiceAccessController} 对IP和服务限制并发数
 - {@link com.logicbus.backend.IpAccessController IpAccessController} 对IP限制并发数
 - {@link com.logicbus.backend.ServiceAccessController ServiceAccessController} 对服务限制并发数

### 服务开发

anyLogicBus下的所有的服务组件均基于{@link com.logicbus.backend.Servant Servant}类，通过其虚方法{@link com.logicbus.backend.Servant#actionProcess(MessageDoc,Context) actionProcess}来实现服务调用的输入和输出。

#### Helloworld

传说中的Helloworld又出现了。

下面的任务是向客户端反馈一条Helloworld信息。

```java
package com.logicbus.examples;
public class Helloworld extends Servant {

	@Override
	public int actionProcess(MessageDoc msgDoc, Context ctx) throws Exception {
		
		//获取服务调用消息
		RawMessage msg = (RawMessage)msgDoc.asMessage(RawMessage.class);
		
		//获取输入输出缓冲区
		StringBuffer buf = msg.getBuffer();
		
		//输出Helloworld到缓冲区
		buf.setLength(0);
		buf.append("Hello world");
		return 0;
	}

}
```

actionProcess方法是服务调用的主函数，函数的参数为：
- MessageDoc 封装了服务调用的输入输出信息
- Context 封装了服务调用的上下文信息，例如客户端IP,Query参数等

服务实现可通过MessageDoc来输入输出信息，如果需要读取参数，则通过Context对象进行。

#### 服务配置参数

有的时候，为了服务模块能够适应多种场合，可以给他定义各种初始化参数。初始化参数从ServiceDescription中配置。下面的例子将展示服务实现如何读取初始化参数，并使用。

例如，现在希望Helloworld中的欢迎语"Hello world"，可以通过初始化参数配置。

```java
package com.logicbus.examples;
public class Helloworld2 extends Servant {
	
	@Override
	public int actionProcess(MessageDoc msgDoc, Context ctx) throws Exception {
		//获取服务调用消息
		RawMessage msg = (RawMessage)msgDoc.asMessage(RawMessage.class);
		
		//获取输入输出缓冲区
		StringBuffer buf = msg.getBuffer();
		
		//输出Helloworld到缓冲区
		buf.setLength(0);
		buf.append(welcome);
		return 0;
	}

	@Override
	public void create(ServiceDescription sd) throws ServantException{
		super.create(sd);
		//获取服务描述的初始化参数表
		Properties props = sd.getProperties();
		//从参数表中获取welcome参数，如果没有配置，缺省值为Hello world
		welcome = props.GetValue("welcome", "Hello world");
	}
	
	/**
	 * 欢迎语
	 */
	protected String welcome;	
}
```
在服务实现中，增加了一个welcome的成员变量，用于保存欢迎语。同时，重载Servant的create函数，在函数中提取初始化参数值，赋值给welcome.

初始化参数可以在服务描述信息中配置，例如：

```xml
	<service 
		id="Helloworld2" 
		name="Helloworld2" 
		note="Helloworld2" 
		visible="protected" 
		module="com.logicbus.examples.Helloworld2"
	>
		<properties>
			<parameter id="welcome" value="welcome to anylogicbus"/>
		</properties>
	</service>
```

#### 服务调用参数
 有的时候，希望从客户端传入参数中读取参数，客户端传入参数有两种方法：
 - 通过URL的Query字串来传递
 - 通过POST在Http请求正文中传递

例如，现在希望能够从服务调用参数中读取欢迎语，客户端须传递一个welcome的参数。
```java
package com.logicbus.examples;
public class Helloworld3 extends Servant {
	@Override
	public int actionProcess(MessageDoc msgDoc, Context ctx) throws Exception {
		//从客户端传入参数列表中读取welcome参数，缺省值为welcome to anylogicbus
		String welcome = getArgument("welcome", "welcome to anylogicbus", msgDoc, ctx);
		
		//获取服务调用消息
		RawMessage msg = (RawMessage)msgDoc.asMessage(RawMessage.class);
		
		//获取输入输出缓冲区
		StringBuffer buf = msg.getBuffer();
		
		//输出Helloworld到缓冲区
		buf.setLength(0);
		buf.append(welcome);
		return 0;		
	}
}
```
样例中，从客户端传入参数列表中读取welcome参数，缺省值为welcome to anylogicbus。如果有些参数是必须的，则采用如下方法：

```java
    String welcome = getArgument("welcome", msgDoc, ctx);
```
在上面的取法中，如果客户端没有传入参数welcome，将会抛出异常client.args_not_found，该方法等同于：
```java
    String welcome = getArgument("welcome","",msgDoc,ctx);
    if (welcome == null || welcome.length() <= 0){
        throw new ServantException("client.args_not_found","Can not find parameter : welcome");
    }
```

上面样例的服务描述信息为：

```xml
	<service 
		id = "Helloworld3" 
		name="Helloworld3" 
		note="Helloworld3" 
		visible="protected" 
		module="com.logicbus.examples.Helloworld3"
	/>
```

#### 服务调用参数的Getter机制

更进一步，anyLogicBus提供了服务调用参数的检查和转换框架。在服务描述中，对一个到多个调用参数进行配置。

例如,利用Helloworld3配置一个Helloworld4.

```xml
	<service 
		id = "Helloworld4" 
		name="Helloworld4" 
		note="Helloworld4" 
		visible="protected" 
		module="com.logicbus.examples.Helloworld3"
	>
		<arguments>
			<argu id="welcome" defaultValue="welcome to anylogicbus again." isOption="true" getter="Default"/>
		</arguments>
	</service>	
```
在没有改动代码的情况下，仅仅在配置服务描述信息时，增加对arguments的配置，即可达到不同的效果。当对某个参数进行了配置之后，代表着启动了Getter机制。

Getter机制的特点如下：

- Getter框架中的defaultValue优先于服务实现中的defaultValue,例如在上例中取到的缺省值为"welcome to anylogicbus again.",而不是"welcome to anylogicbus"
- 如果isOption标记为true，defaultValue才有效，反之会抛出client.args_not_found异常
- gettter属性代表着一个Getter实现类，可以自己定义，可以在其中进行类型检查，或者转换

缺省的Getter实现为com.logicbus.models.servant.getter.Default，其代码为：

```java
public class Default implements Getter {

	@Override
	public String getValue(Argument argu, MessageDoc msg, Context ctx) throws ServantException {
		String id = argu.getId();
		String value;
		if (argu.isOption()){
			value = ctx.GetValue(id, argu.getDefaultValue());
		}else{
			value = ctx.GetValue(id, "");
			if (value == null || value.length() <= 0){
				throw new ServantException("client.args_not_found",
						"Can not find parameter:" + id);
			}
		}
		return value;
	}
}
```

例如可以开发一个类型转换Getter，在所有取到的值上增加"Hello".

```java
package com.logicbus.examples;
public class AppendHello extends Default {
	@Override
	public String getValue(Argument argu, MessageDoc msg, Context ctx) throws ServantException {
		return super.getValue(argu, msg, ctx) + "hello";
	}
}
```
在此基础上，又可以形成一个不同的服务。
```xml
	<service 
		id = "Helloworld5" 
		name="Helloworld5" 
		note="Helloworld5" 
		visible="protected" 
		module="com.logicbus.examples.Helloworld3"
	>
		<arguments>
	    	<argu id="welcome" defaultValue="welcome to anylogicbus again." isOption="true" getter="com.logicbus.examples.AppendHello"/>
		</arguments>
	</service>	
```

目前anyLogicBus只有一个Default的Gettter，更多更强大的Getter期待你去实现。

#### 输入输出消息

在前面的例子中，反复出现了一段代码：
```java
    RawMessage msg = (RawMessage)msgDoc.asMessage(RawMessage.class);
```
在anyLogicBus中MessageDoc封装了服务调用的输入输出文档，但anyLogicBus不建议直接对MessageDoc进行输入输出操作，而是委托给Message进行操作，在Message中可以扩展支持多种消息协议，例如JSON,XML等。其中RAWMessage也是一种消息协议，只不过是最为原始，简单的消息协议，采用text/plain类型。

与RAWMessage类似，anyLogicBus实现了基于XML协议的消息。

还是以Helloworld为例，前面的案例是以纯文本通讯，下面的例子采取了XML.

```java
package com.logicbus.examples;
public class Helloworld6 extends Servant {

	@Override
	public int actionProcess(MessageDoc msgDoc, Context ctx) throws Exception{
		XMLMessage msg = (XMLMessage)msgDoc.asMessage(XMLMessage.class);		
		
		Element root = msg.getRoot();
		Document doc = root.getOwnerDocument();
		
		Element hello = doc.createElement("Hello");
		hello.appendChild(doc.createTextNode("Hello world"));
		root.appendChild(hello);
		return 0;
	}
}
```
在XMLMessage实现模式下，输入输出都是针对该XML树进行操作。

#### 定制消息协议

anyLogicBus可以轻松的实现自己定制的消息协议。实际上，anyLogicBus也实现了基于JSON的协议，但由于需要附加另外的JSON解析器，所以没有放在核心版本中发布。

JsonMessage在一个基于anyLogicBUs的项目anyPaas中实现了。下面是其代码：

```java

package com.anypaas.service;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.anysoft.util.IOTools;
import com.anysoft.util.JsonTools;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.Message;
import com.logicbus.backend.message.MessageDoc;

public class JsonMessage extends Message {

	/**
	 * Json结构的根节点
	 */
	protected Map<String,Object> root = null;
	
	@SuppressWarnings("unchecked")
	public JsonMessage(MessageDoc _doc,StringBuffer _buf) {
		super(_doc);
		
		String data = _buf.toString();
		if (data != null && data.length() > 0){
			JsonProvider provider = JsonProviderFactory.createProvider();
			Object rootObj = provider.parse(_buf.toString());
			if (rootObj instanceof Map){
				root = (Map<String,Object>)rootObj;
			}
		}
		if (root == null){
			root = new HashMap<String,Object>();
		}
		setContentType("application/json;charset=" + msgDoc.getEncoding());
	}

	public Map<String,Object> getRoot(){return root;}
	
	@Override
	public void output(OutputStream out, Context ctx) {
		JsonTools.setString(root, "code", msgDoc.getReturnCode());
		JsonTools.setString(root, "reason", msgDoc.getReason());
		JsonTools.setString(root, "duration", String.valueOf(msgDoc.getDuration()));
		JsonTools.setString(root, "host", ctx.getHost());
		
		JsonProvider provider = JsonProviderFactory.createProvider();
		String data = provider.toJson(root);
		try {
			out.write(data.getBytes(msgDoc.getEncoding()));
		}catch (Exception ex){
			ex.printStackTrace();
		}finally {
			IOTools.closeStream(out);
		}
	}

	@Override
	public boolean hasFatalError(){
		return false;
	}	
}
```

基于JsonMessage的Helloworld版本为：
```java
package com.logicbus.examples;
public class Helloworld7 extends Servant {

	@Override
	public int actionProcess(MessageDoc msgDoc, Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage)msgDoc.asMessage(JsonMessage.class);		
		Map<String,Object> root = msg.getRoot();
		root.put("Hello","Hello world");
		return 0;
	}
}
```


