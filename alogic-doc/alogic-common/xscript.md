## XScript

xscript是alogic支持的一种基于XML的脚本语言，用于在JVM内部运行批处理逻辑。

###  更新历史

- alogic-1.6.3.25 [20150529 duanyy] 统一脚本的日志处理机制,参见日志处理小结

### 语法树
xscript采用XML解析器作为词法和语法解析器，在XMLDOM基础上进行语义解析。xscript最为基础的运行逻辑为Statement，其中有一种特殊的Statement为Block，定义了脚本块。Block和一般的Statement构成了xscript的语法树结构。

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

由上面的代码可以看出，xscript的Statement是通过XMLDOM中的Element来定义的，Element的Tag就是Statement的名称。对于常规编程语言中的代码块，采用Block来定义（上面的script和segment都是继承自Block）。

### 一切都是插件
在xscript中，一切Statement都是插件，对应着一个Java实现类。xscript包含了一些列的内置插件，包括：
- 使用插件(using)
- 代码块(script,segment)
- 变量(var)
- 多线程(async,sleep)
- 条件(choose,switch)
- 异常处理(throw,except,finally)
- 包含(include)

除了这些内置插件，还可用通过using语句来引入自定义插件，例如：
```xml
	<using
		xmlTag="helloworld"
		module="com.anysoft.xscript.Helloworld"
	/>
```
然后，helloworld这个插件可以直接在xscript使用。
```xml
	<helloworld/>
```

### 代码块
xscript代码块相当于java语言中的{}，定义了一个Statement的组合。例如，下列的Java代码：
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

> segment和script并没有明显区别，我们只是在代码上，约定script应该作为语法树的根节点，而处理中间位置的代码块，则用segment。实际上，script是继承自segment，并且没有自己的实现。

segment维护了一个Statement列表，在执行的时候，依次对该列表中的Statement进行执行。

> Statement如何执行取决于Block的实现，例如在异步调用Block（即语句async）中，对Statement进行多线程同时执行。

和其他编程语言一样，代码块除了能形成良好的编码风格之外，也将影响局部变量的作用域。

### 变量定义
xscript支持进行变量定义，但变量只有一种类型，即字符串，并且只在当前代码块及其子Statement中有效。

看看下列代码：
```xml
	<script>
		<segment id="segment">
			<var id="name" selector-value="alogic"/>
			<log msg="the variable is '${name}'"/>
			<segment id="child">
				<log msg="the variable is '${name}'"/>
			</segment>
		</segment>
		<log msg="the variable is '${name}'"/>
	</script>
```

在上面的代码中，首先在segment中，定义了一个id为name的变量，取值为alogic；接着，在当前代码块中，通过log语句打印出该变量的值；接着在当前代码块的子代码块(child)中，通过Log语句打印出该变量的值；最后在代码块之外，再次通过log语句打印该变量值。

可以看到结果如下；
```
the variable is 'alogic'
the variable is 'alogic'
the variable is ''
```
也就是说在代码块之外是取不到这个变量的，而在当前代码块和子代码块中均可取到这个变量。

在上面的案例中，对name变量是一个静态赋值。实际上，var插件的内部实现采用了selector框架（com.anysoft.selector，另行说明），selector框架是一个强大的变量计算框架，可以从变量集(Properties)中按照规则计算出想要的结果，应用到var插件中，那就意味着可以针对当前变量集和脚本调用参数计算出自己想要的值，并赋值给变量。

### 多线程
前面提到Segment对其子Statement列表是按照次序顺序同步执行的。那么问题来了，能否支持多线程，进行异步执行？

这就用到async语句了。

async语句也是一个Block实现，只不过他按照子Statement列表中Statement个数分为多个线程，每个Statement获取一个线程，从而同步执行。

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

### 条件语句
作为一门语言，条件语句是少不了的啦。xscript中的条件语句包括：choose和switch。

choose类似于java中的ifelse句式，用法如下：
```xml
	<choose>
		<foo1/>
		<foo2/>
	</choose>
```
和var类似，choose本身通过selector机制计算出一个逻辑判断值，如果该值不为false，则执行foo1，反之则执行foo2，其中foo2是可选的（即if后面可以没有else）。

switch类似于java中的switch句式，用法如下：
```xml
	<switch>
		<foo1 case="case1"/>
		<foo2 case="case2"/>
		<foo3 case="case3"/>
		<foo4 case="default"/>
	</switch>
```
switch本身通过selector机制计算出一个值，该值将和子Statement列表中的case进行比对，如果匹配，则执行对应的Statement，如果匹配不到，则执行case值为default的Statement（如果没有定义default的case，则不做任何事）。

### 循环语句

循环语句？还没想好！！！

### 异常处理
异常处理是任何一门语言所应该具备的。xscript不支持通过检测Statement的执行返回值来检测异常，xscript支持各Statement实现类所抛出的BaseException异常。

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
代码块中允许定义一个或多个except处理块，每个处理块需要定义id以便和其他处理块相区别，这个id就是本代码块中其他代码所抛出的异常代码（参见com.anysfot.util.BaseException的实现），当没有配置id的时候，采用一个缺省的特殊ID，即“except"。

处理逻辑如下：当本代码块或者子代码块抛出BaseException异常的时候，获取该异常的code，然后以该code搜索所注册的except处理块，如果找到，则执行该处理块，如果没有找到，则查找id为except的缺省处理块，如果找到，则执行该缺省处理块，如果还是没有找到，则向父代码块抛出该异常。

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
从alogic-1.6.3.29开始，提供了include语句。

include用于包含另外一个xscript脚本,用法如下：
```
	<include src="<url>"/>
```

### 日志处理
从alogic-1.6.3.25开始，提供了统一的日志处理机制。

脚本中，日志的输出可以通过log语句来进行日志的输出，例如：
```
<log msg="Helloworld" progress="100" level="info" id="activity1"/>
```
上述语句中，msg参数定义了要输出的信息模板（请注意是模板，模板是支持变量的）；progress定义当前处理的进度，它是一个整型值（从-2至10001,-2代表非进度,-1代表还没开始,10001 代表已经完成,0-10000 代表以10000为基数的百分比），缺省为-2；level定义了日志的级别，分为三个级别：error,warn,info；id定义了当前活动的id，缺省为语句id(即log)。

这就意味着，你可以在脚本中向当前的日志处理器输出信息。

在上一个版本中，log语句是通过log4j简单的实现了日志的输出，现在有了一个的日志处理器ScriptLogger。

采用logger语句来定义ScriptLogger，例如:
```
	<logger module="com.anysoft.xscript.ScriptLogger$Default"/>
```
上面的代码定义了一个缺省实现的日志处理器（实现为com.anysoft.xscript.ScriptLogger$Default，采用log4j做了简单实现）

> ScriptLogger和BizLogger类似，采用了一个基于有向树的流处理框架(com.anysoft.stream)，你可以定制ScriptLogger插件来实现日志的处理。

值得注意的是，logger是定义在代码块下面的，也就是说每个代码块都可以定义一个自己的logger，如果代码块没有定义自己的logger，日志信息会被向父语句节点传递。对于根节点（script语句），如果没有定义logger，则会自动创建一个缺省实现的logger（即com.anysoft.xscript.ScriptLogger$Default）。

### 如何使用xscript？
上面已经介绍了这个简单而强大的全新的脚本语言，那么问题来了，如何使用？

alogic提供了XScriptTool类(com.anysoft.xscript.XScriptTool)来使用xscript。xscript的使用分为两步：

* **编译**
xscript的编译过程是将一个XML资源或XMLDOM编译为xscript的语法树。

```java
	/**
	 * 将Xml编译为语句
	 * 
	 * @param root XML节点
	 * @param p 编译参数
	 * @param watcher 编译监视器
	 * @return 语句实例
	 */
	public static Statement compile(Element root,Properties p,CompileWatcher watcher);

	/**
	 * 将URL所指向的XML文档编译为语句
	 * @param url URL
	 * @param p 编译参数
	 * @param watcher 编译监视器
	 * @return 语句实例
	 */
	public static Statement compile(String url,Properties p,CompileWatcher watcher);
```
XScriptTool提供了两个静态方法来进行编译，两个方法其实是同一回事，方法一是直接对XMLDOM进行编译，而方法二是通过同一资源装入来装载指定的XML文档。

方法的返回是一个Statement对象，为script语句所对应的对象，代表了一个xscript语法树。该对象可以直接进行执行。

* **执行**
xscript的执行过程是通过执行参数集来执行Statement对象。

```java
	/**
	 * 执行语句
	 * @param stmt 语句
	 * @param p 执行参数
	 * @param watcher 观察者
	 * @return 执行结果
	 */
	public static int execute(
		Statement stmt,
		Properties p,
		ExecuteWatcher watcher);
```
XScriptTool提供了一个静态方法来执行脚本。Statement是compile方法所生成的语法树。Properties是执行参数集，保存了本次执行所需的参数变量。

下面是一个执行xscript脚本的Demo.

```java
	public static void main(String[] args) {
		//脚本所在的XML位置
		String url = "java:///com/anysoft/xscript/Helloworld.xml#com.anysoft.xscript.Demo"

		//编译该脚本
		Statement stmt = XScriptTool.compile(url, Settings.get(),new CompileWatcher.Default());

		//准备执行参数
		Properties p = new DefaultProperties(
			"Default",Settings.get()
		);
		p.SetValue("id", "alogic");
		p.SetValue("name", "alogic");

		//执行脚本
		XScriptTool.execute(stmt, p, new ExecuteWatcher.Default());
	}
```

这里还有两个有趣的东西ExecuteWatcher和CompileWatcher。xscript提供一个接口，让你可以直接观察其语法树的执行情况，通过实现该接口，你可以监控脚本的执行情况，执行进度，执行效率等。

```java
/**
 * 执行监视器
 * 
 * @author duanyy
 * @since 1.6.3.22
 */
public interface ExecuteWatcher {
	/**
	 * Statement执行完成
	 * 
	 * @param statement 语句
	 * @param p 执行参数
	 * @param start 开始时间
	 * @param duration 结束时间
	 */
	public void executed(
		Statement statement,
		Properties p,
		long start,
		long duration
	);
}
```

同样道理，CompileWatcher用于监控脚本的编译情况。

### 如何开发xscript插件？
xscript的另外一个令你兴奋功能就是强大的插件机制。你可以定制自己的插件，从而不断的扩充xscript的功能。

开发插件的方法就是实现Statement接口。
```xml
/**
 * 脚本语句
 * 
 * @author duanyy
 * @since 1.6.3.22
 */
public interface Statement extends Reportable{
	
	/**
	 * 编译语句
	 * @param e 对应的XML节点
	 * @param p 编译参数
	 * @param watcher 编译监控器
	 * @return 编译结果
	 * 
	 * @since 1.6.3.23
	 */
	public int compile(Element e,Properties p,CompileWatcher watcher);
	
	/**
	 * 执行语句
	 * @param p 参数
	 * @param watcher 执行监控器
	 * @return 执行结果
	 */
	public int execute(Properties p,ExecuteWatcher watcher) throws BaseException;
	
	
	/**
	 * 获取父语句
	 * @return 语句
	 */
	public Statement parent();
	
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
	 * 通过xml tag创建Statement实例
	 * 
	 * @param xmlTag xmltag
	 * @return Statement实例
	 */
	public Statement createStatement(String xmlTag,Statement parent);
	
	/**
	 * 注册Statement的实现模块
	 * <p>
	 * 所注册的module在该节点及其子节点有效。
	 * 
	 * @param xmltag xml tag
	 * @param clazz Class实例
	 */
	public void registerModule(String xmltag,Class<?extends Statement> clazz);
	
	/**
	 * 注册异常处理模块
	 * @param id 异常code
	 * @param exceptionHandler 异常处理段
	 */
	public void registerExceptionHandler(String id,Statement exceptionHandler);
}
```
但是通常，我们都是基于AbstractStatement来定制插件，AbstractStatement已经帮你完成了Statement接口中令你迷惑的功能，让你专注自己的业务逻辑。

没错，这个时候Helloworld又出现了。

我们要定制一个Helloworld插件，在log4j中输出一些东西。
```java
public class Helloworld extends AbstractStatement {

	public Helloworld(String _tag, Statement _parent) {
		super(_tag, _parent);
	}

	@Override
	protected int onExecute(Properties p, ExecuteWatcher watcher) throws BaseException {
		logger.info("java.vm.name=" + PropertiesConstants.getString(p,"java.vm.name",""));
		logger.info("java.vm.version=" + PropertiesConstants.getString(p,"java.vm.version",""));
		logger.info("java.vm.vendor=" + PropertiesConstants.getString(p,"java.vm.vendor",""));
		return 0;
	}

	protected int compiling(Element e, Properties p, CompileWatcher watcher) {
		return 0;
	}
}
```
基于AbstractStatement，你只需要完成两件事。第一，实现compiling方法来读取自己所需的配置，该方法在compile期间被调用；第二，实现onExecute方法来做自己的事情。

在alogic-1.6.3.25中，我们增加了日志处理机制。在定制开发中，可以通过AbstractStatement的日志输出方法输出日志。

```
	public void log(ScriptLogInfo logInfo){
		if (parent != null){
			parent.log(logInfo);
		}
	}
	
	public void log(String message,String level,int progress){
		log(new ScriptLogInfo(activity,message,level,progress));
	}
	
	public void log(String message,String level){
		log(message,"info",-2);
	}
	
	public void log(String message,int progress){
		log(message,"info",progress);
	}
	
	public void log(String message){
		log(message,-2);
	}
```

### 变量体系
在前面的章节中，我们反复遇到变量集，执行参数集等等。这些令人疑惑的东西到底是怎么回事？

不管前面怎么称呼在此统一说明。
* **执行参数集**
执行参数集指的是在执行脚本之前所准备的参数集合，类似于编程语言中存放在heap中的全局变量，例如上文Demo中的Properties中的变量。
```java
	//准备执行参数
	Properties p = new DefaultProperties(
		"Default",Settings.get()
	);
	p.SetValue("id", "alogic");
	p.SetValue("name", "alogic");
```
* **本地变量集**
本地变量集指的是当前Block作用域所维护的一个变量集，类似于编程语言中的存放在stack中的本地变量。

在语法树中，每一个Block都维护了一个本地变量集，并且根据层次关系形成一个链表，最底层的变量没有搜索到，会自动向上层搜索。链表的最上层就是执行参数集。

在这种机制下，形成这样的效果：
* 所有的Statement都能使用执行参数集，除非本地参数集中有同ID的参数覆盖了该参数；
* 代码块的本地变量集作用域限定在当前代码块及其子代码块；
* 所有的Statement都能使用父代码块以上层次的本地参数集，除非本地参数集中有同ID的参数覆盖了该参数。

上述的变量体系由变量集框架(com.anysoft.util.Properties，另行说明)提供。

### 后记
后面就交给你，请用xscript实现你的业务逻辑吧！
