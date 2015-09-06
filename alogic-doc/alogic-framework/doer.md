## 定时小任务组件

alogic-doer是alogic中负责定时小任务的组件。所谓小任务，指的是能够在单jvm中处理的批处理任务，解决两个问题：
- 如何触发任务的执行？即常规意义上的定时器(timer)，根据一定的时间匹配规则来触发和执行任务；
- 如何定制化任务？即开发者如何定制自己的任务逻辑。

## 更新记录
- [20150902 zhangzundong] 创建文档;
- [20150906 duanyy] 修改文档;

## 定时器(timer)
定时器timer是alogic-doer中的一个模块，用于根据规则触发任务的执行。

### 如何启动Timer
首先看一个简单的例子
```Java
	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler.Simple();//建立一个scheduler
		scheduler.setTaskCommitter(new ThreadPoolTaskCommitter());//设定任务提交者

        //设定调度任务信息
		scheduler.schedule("testOnce", new Once(), new Runnable(){
			public void run() {
				System.out.println("This will be scheduled once.");
			}
		});
		scheduler.schedule("testMore", new Interval(1000), new Runnable(){
			public void run() {
				System.out.println("testMore.");
			}
		});
		
		scheduler.start();
		
		scheduler.join(20000);
		
		scheduler.stop();
	} 
```
使用定时器时，首先我们要建立一个任务调度者scheduler并设定它的任务提交者，然后通过schedule方法设置所需执行的任务信息。例子中两个任务分别按照匹配器Once()和匹配器Interval()的规则去调度指定的Runnable.设定好之后启动这个scheduler就完成了(schedule的配置方法有很多种，可参考alogic\timer\core\Scheduler.java）。

事实上，更多情况下我们会使用工厂模式去实例化scheduler，方法如下：
```
    Scheduler scheduler = SchedulerFactory.get();
```
### Web下的启动方式
Scheduler通过一个ServletContextListener来实现Web下的启动。该ServletContextListener为:
```
	com.alogic.timer.webapp.Bootstrap
```

对于标准的Web应用，可通过配置web.xml的ContextListener来完成。

对于ketty的应用，可以通过以下方式实现：
首先在setting中添加环境变量webcontext.addons；
```
	<parameter id="webcontext.addons" value="java:///conf/web.addons.xml#App"/>
```
然后在web.addons.xml中添加
```
    <webapp-addons>
		<listener listener-class="com.alogic.timer.webapp.Bootstrap"/>
    </webapp-addons>
```
这样服务运行后Listener会通过工厂模式实例化并启动Scheduler。

### Timer的配置
Scheduler是一个全局的对象，通过SchedulerFactory来创建，通过环境变量(timer.master)所指向的配置文件进行配置。

使用工厂模式初始化scheduler的时，会读取timer.master参数中的内容。缺省为:
```
    <parameter 
    	id="timer.master" 
    	value="java:///com/alogic/timer/core/timer.xml#com.alogic.timer.core.Timer"
    />
```

通常，可以通过配置文件来配置timer.master，必要时，也可以使用如下方法：
```
	Settings settings = Settings.get();
	settings.SetValue("timer.master","java:///路径#类名");
```

接下来，看一个Timer配置案例
```
    <scheduler module="com.alogic.timer.core.Scheduler$XMLed">
		<committer module="com.alogic.timer.core.ThreadPoolTaskCommitter"/>
		<timer id = "test">
		    <matcher module="Interval" interval="1000"/>
		    <doer module="com.alogic.timer.core.Doer$Quiet"/>
		</timer>
    </scheduler>
```

上面的配置文件包含了以下信息：

- 指定配置环境scheduler和committer
- 创建定时器并创建其id，案例中id为test
- 为定时器指定匹配器，并设定匹配器的参数，案例中匹配器为Interval，参数interval的值为1000
- 配置环境doer，案例中的doer的环境为com.alogic.timer.core.Doer$Quiet（其实就是什么都不干，不建议在此设置有内容的Doer，因为事实上这没什么意义，Doer在哪设置下面会说到）。

从上面配置可以看到，一个定时器(timer)包含了2部分：匹配器(matcher)和执行器(doer).

### 匹配器(Matcher)
匹配器在上文中有提到过，用于确定Timer的触发时机。alogic-doer目前一供提供了8种匹配器：

- Once 触发一次，无参数
- Counter 按时间间隔触发一定次数，支持参数count，interval
- Interval 按时间间隔触发，支持参数interval
- Hourly 每小时触发，支持参数minutes
- Daily 每天触发，支持参数minutes，hours
- Weekly 每周触发，支持参数minutes，hours，days
- Monthly 每月触发，支持参数minutes，hours，days
- Crontab 标准定时器，共5个配置，支持参数crontab

#### 在XML中匹配器的配置方法
上文以提到过matcher在XML中的配置样例
```
    <matcher module="Interval" interval="1000"/>
```

配置时首先通过module指定使用何种匹配器，之后对每种参数进行配置。
每种参数的具体配置方法如下：

- count 次数，支持自然数
- interval 时延，单位为毫秒
- minutes 分钟，为0-59之间的整数
- hours 小时，0-23之间的整数
- days 注意Weekly的days与Monthly中的days不同，Weekly的days除了支持1-7的整数，还支持星期的英文缩写，且不区分大小写，例如：Mon,Tue,Web,Thu,Fri,Sat,Sun等（并不支持全称）。Monthly中的days支持1-31之间的整数。
- crontab 一共五个参数，每个参数之间使用空格分开，五个参数依次是minute hour day-of-month month-of-year day-of-week,其中day-of-week与Weekly中的days相同，month-of-year支持0-12的整数和英文月份的缩写如Jan,Feb,Mar,Apr,May,June,July等。

让我们再看一个Crontab的例子
```
    <matcher module="Crontab" crontab="0 * 14,15,16 Aug,sept fri,sat,sun">
```
这个crontab指定参数的触发时间是八月和九月中是星期五、星期六或星期天的14，15，16号的每个小时触发。

#### 使用函数构造匹配器
在某些情况下，可以在代码中构造匹配器。

```Java
    scheduler.schedule("testMore", new Interval(1000), new Runnable(){
            public void run() {
                System.out.println("testMore.");
            }
        });
```

#### 定制化的匹配器
如果现有的匹配器无法满足要求，可以自定制匹配器，参见附录1：匹配器的定制化。

### 执行器(Doer)

执行器负责业务逻辑的执行，例如前面案例中的com.alogic.timer.core.Doer$Quiet.在实际系统中，通常都是由开发人员来定制执行器，完成自己的业务逻辑。

#### 执行器接口
alogic-doer通过接口Doer来定义执行器，如下：

```
public interface Doer extends Configurable,XMLConfigurable,Runnable,Reportable{
	
	/**
	 * 状态
	 * 
	 * @author duanyy
	 *
	 */
	public enum State{
		/**
		 * 空闲
		 */
		Idle,
		/**
		 * 工作中
		 */
		Working
	}
	
	/**
	 * 获取Doer状态
	 * @return
	 */
	public State getState();
	
	/**
	 * 设置上下文持有者
	 * @param holder 持有者
	 */
	public void setContextHolder(ContextHolder holder);
	
	/**
	 * 获取上下文持有者
	 * @return holder
	 */
	public ContextHolder getContextHolder();
	
	/**
	 * 设置当前的任务
	 * @param task 任务
	 */
	public void setCurrentTask(Task task);
	
	/**
	 * 获取当前的任务
	 * @return 当前任务
	 */
	public Task getCurrentTask();
	
	/**
	 * 设置任务状态监听器
	 * @param listener 监听器
	 */
	public void setTaskStateListener(TaskStateListener listener);
	
	/**
	 * 执行
	 * @param task 待执行的任务
	 */
	public void execute(Task task);
	
	/**
	 * 完成任务
	 */
	public void complete();
}

```

#### 定制Doer

通常为了方便，我们建议去继承Doer的抽象类Doer.Abstract，例如：
```
	public interface Doer extends Configurable,XMLConfigurable,Runnable,Reportable{
		public static class Quiet extends Abstract{
			public void execute(Task task) {
				// do nothing
			}
		}
	}
```
通过抽象类Doer.Abtract，你只用去实现execute方法，关注你的业务逻辑。

如果你需要从XML配置文件中获取配置信息，可以重写configure方法。

```
	public void configure(Element _e, Properties _properties)
		throws BaseException {
		Properties p = new XmlElementProperties(_e,_properties);
		configure(p);			
	}		
		
	public void configure(Properties p) throws BaseException {
		// nothing to do
	}
```

#### Runnable包裹器

在传统的定时器模式下，定时器任务通常是通过Runnable接口来实现的，alogic-doer提供了Runnable的包裹器。

```
	public interface Doer extends Configurable,XMLConfigurable,Runnable,Reportable{
		public static class Wrapper extends Abstract{
			protected Runnable real = null;
			
			public Wrapper(Runnable runnable){
				real = runnable;
			}
			
			public void configure(Properties p) throws BaseException {
				// nothing to do
			}
	
			public void execute(Task task) {
				if (real != null){
					real.run();
				}
			}
		}
	}
```
利用该包裹器，你可以快速的将Runnable实现的定时器迁移到alogic-doer中，参见开始的例子：
```
	scheduler.schedule("testOnce", new Once(), new Runnable(){
		public void run() {
			System.out.println("This will be scheduled once.");
		}
	});
```

#### 脚本执行器(ScriptDoer)
alogic-doer还实现了一种脚本执行器，直接对接xscript脚本。例如：
```
    <doer module="com.alogic.timer.core.ScriptDoer">
    	<script>
    		<log msg="Hello World！"/>
    		<sleep timeout="1000"/>
    	</script>
    </doer>
```
你可以不去定制Doer，而是通过xscript脚本完成你的业务逻辑。

## 后记
至此，你看到的和Quartz并没有什么两样，似乎一切已经完备。

但对于alogic-doer来说，这只是开始，一切都是分布式下小任务执行框架的基础。

如何进行分布式处理，且听下回分解。

## 附录：匹配器的定制化
### Matcher接口 ###
如果你觉得目前提供的匹配器功能不能满足需求，可以进行定制化开发。

alogic使用Matcher接口来处理匹配器功能。Matcher的定义如下：

```Java
	public interface Matcher extends Configurable,XMLConfigurable,Reportable {
		
		/**
		 * 是否匹配
		 * @param _last 上次调度时间
		 * @param _now 当前时间
		 * @param _ctxHolder 上下文持有人
		 * @return true|false
		 */
		public boolean match(Date _last,Date _now,ContextHolder _ctxHolder);
		
		/**
		 * 是否可以清除
		 * 
		 * <br>
		 * 如果返回为true，框架将从列表中清除该Timer
		 * @return true/false
		 */
		public boolean isTimeToClear();		
	}
```
如果要定制一个匹配器，可以自定义一个类来实现接口Matcher。通过为了方便，我们建议去继承Matcher的抽象类Matcher.Abstract.

### 一个简单的Mactcher

我们以Interval匹配器做为例子来看看：

```Java
    public class Interval extends Matcher.Abstract {
		/**
		 * 间隔时间，可通过参数interval配置
		 */
		protected long interval = 1000;
		
		public Interval(){
		}
		
		public Interval(long _interval){
			interval = _interval;
		}
		
		public void configure(Properties p) throws BaseException {
			interval = PropertiesConstants.getLong(p,"interval",interval);
		}
	
		public boolean match(Date _last, Date _now,ContextHolder ctx) {
			return ((_now.getTime() - _last.getTime()) >= interval);
		}
	
		public boolean isTimeToClear() {
			//never clear
			return false;
		}
	}
```
Interval的实现相当简单，使用configure获取参数interval的值，在match中实现自己的匹配逻辑，由于没有触发上限所以不会清除。

alogic还提供了一个基于可选值的集合的日期值匹配器SetValueMatcher，你可以使用它来对日期进行存储和判断，我们来看一个例子

```Java
    public boolean match() {
    	//使用DayofMonth解释器将日期1，2号存储
		SetValueMatcher daysOfMonth = new SetValueMatcher("1,2",new DayOfMonth());
		Calendar calendar = Calendar.getInstance();
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		return daysOfMonth.match(dayOfMonth);//和当前日期进行匹配
	}
```

储存数据时需要用到相应的解释器，目前解释器有五种minute、hour、day-of-month、month-of-year、day-of-week，语法规则参照匹配器中参数的设置。

