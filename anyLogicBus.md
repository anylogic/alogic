anyLogicBus readme
==================

一个高效的轻量级服务框架

### 特征
anyLogicBus是一个高效的,可扩展的,轻量级服务框架,具有下列特征:
 
 - 服务路由，将HTTP请求分发给具体服务模块；
 
 - 访问控制，支持多种访问控制策略，并且可定制；
 
 - 插件式服务模块，提供服务规范，支持定制开发
 
 - 可定制的服务目录，支持多种形式的服务目录
 
 - 内置多种管理服务
 
 - 可部署成集群管理模式，分为元数据服务器，监控服务器，服务节点进行部署
 
 - 基于anyWebLoader自动更新框架
 
 - 面向协议的服务组装和并发调度框架,从1.1.0起提供
 
### 版本
 
 - 1.0.0 [20140327 duanyy]
     + 首次发布
     
 - 1.0.1 [20140402 duanyy]
     + 改进访问控制模型，以避免SessionID多次计算
     
 - 1.0.2 [20140407 duanyy]
     + 修改{@link com.logicbus.backend.server.MessageRouter MessageRouter},
     采用{@link java.util.concurrent.CountDownLatch CountDownLatch}来等待服务执行。
     
 - 1.0.3 [20140410 duanyy]
     + 在{@link com.logicbus.models.servant.ServiceDescription ServiceDescription}中增加调用参数列表
     + 在{@link com.logicbus.backend.Servant Servant}增加调用参数读取的封装函数 
 
 - 1.0.4 [20140410 duanyy]
     + 增加Raw消息，见{@link com.logicbus.backend.message.RawMessage RawMessage}
     + 增加客户端调用框架，见{@link com.logicbus.client Client}
     
 - 1.0.5 [20140412 duanyy]
     + 改进消息传递模型
     + 增加开发文档(development.md)，并增加一些案例(com.logicbus.examples)
     
 - 1.0.6 [20140417 duanyy]
     + 增加数据库连接池(com.logicbus.datasource)
     
 - 1.0.7 [20140418 duanyy]
     + 增加全局序列号生成及传递机制
     + 修改HttpClient的对Http头的输入输出机制
 
 - 1.0.8 [20140420 duanyy]
     + {@link com.logicbus.models.servant.Getter Getter}增加从Message中获取参数的接口。
     
 - 1.1.0 [20140422 duanyy]
     + 首次发布面向协议的服务组装和并发调度框架(together)
     + 增加内置服务Simulator,模拟服务执行,用于框架的并发测试
     + 修正数据源中jdbc驱动注册问题
     
 - 1.1.1 [20140423 duanyy]
 	 + 修改ConnectionFactory,在创建数据库连接前,进行各项参数的确认,通常用于将数据库密码分开保存

 - 1.1.2 [20140504 duanyy]
 	 + 增加JMS支持，支持activeMQ
 
 - 1.1.3 [20140605 duanyy]
 	 + 增加会话管理框架
 	 + 增加数据库操作工具类
 	 
 - 1.2.0 [20140606 duanyy]
 	 + together增加对JSON的支持
 	 
 - 1.2.1 [20140613 duanyy]
 	 + 增加JsonMessage来支持JSON协议
 	 + 优化logiclet执行监控器的接口
 	 + 支持跨域服务调用(CORS)
 
 - 1.2.2 [20140617 duanyy]
 	 + 改进核心调度模块的同步模型
 	 + 改进Client相关的API
 	 
 - 1.2.3 [20140617 duanyy]
 	 + 优化全局变量的命名
 	 + 优化访问控制模型
 	 + 优化Client端API
 	 + 增加日志采集框架
 	 
 - 1.2.4 [20140705 duanyy]
 	 + Servant缓冲池采用队列来实现
 	 + HttpClient可以根据Response的Content-Type调整encoding (1.2.4.1)
 	 + Parameter增加encoding方法(1.2.4.2)
 	 + ACMAccessController在找不到ACM的情况下,使用缺省的ACM(1.2.4.3)
 	 + ServiceDescription增加LogType的设置方法(1.2.4.4)
 	 + ServiceDescription增加properties和arguments的设置方法(1.2.4.4)
 	 + LogicBusApp在初始化的时候增加装入扩展的配置文件(1.2.4.5)
 	
 - 1.2.5 [20140722 duanyy]
     + 基础包(com.anysoft.pool)有更新
     + 重写dbcp实现
     + 修正ResourceFactory的bug(1.2.5.1)
     + 增加JVM环境变量的查询服务(1.2.5.2)
     + 基础包的Cacheable接口修改(1.2.5.3)
     + 服务描述通过接口实现(1.2.5.4)
     + 参数通过接口实现(1.2.5.4)
     
 - 1.2.6 [20140807 duanyy]
     + ServantPool完全插件化(1.2.6)
     + 优化JMS框架(1.2.6.1)
     + 优化ACM访问控制模型的缺省模型存储机制(1.2.6.2)
     + 配合基础类库Pool的修改(1.2.6.3)
     + MessageRouter:修正servant实例无法获取到，抛出NullPointException问题(1.2.6.4)
     + LogicBusApp:增加onInit/onDestroy事件，以便子类更好的进行初始化和销毁(1.2.6.5)
     
 - 1.2.7 [20140829 duanyy]
	 + 重写BigLogger(1.2.7)
	 + 增加BizLogger数据统计框架(1.2.7.1)
 	 + 增加BizLogger的pause和resume的控制服务(1.2.7.2)
 	 + 优化BizLogger的统计模型(1.2.7.2)
 	 + 增加核心服务BizLoggerPause,用于暂停BizLogger的处理(1.2.7.2)
 	 + 增加核心服务BizLoggerResume,用于恢复BizLogger的处理(1.2.7.2)
 	 + LogicBusApp:去掉Normalizer，将Normalizer降级为Servlet级别对象(1.2.7.2)
 	 + Normalizer:增加代理URL语法的解析实现（ProxyNormalizer和AppProxyNormalizer）(1.2.7.2)
 	 + 增加核心服务Proxy,用于服务代理(1.2.7.2)
 	 + 修正bizlog日志中client的取值(1.2.7.2)
 
 - 1.2.8 [20140912 duanyy]
  	 + 增加监控指标收集体系(1.2.8)
  	 + JsonSerializer中Map参数化(1.2.8)
  	 + 增加核心服务:MetricsReport,用于报告Metrics处理统计信息;MetricsPause,用于暂停MetricsHandler的处理;MetricsResume,用于恢复MetricsHandler的处理(1.2.8)
  	 + 增加Selector机制(com.logicbus.selector)(1.2.8)
  	 + 淘汰BizLogger数据统计框架(1.2.8)
  	 + 淘汰MetricsHandlerContext框架(1.2.8.1)
  	 + MetricsHandler:getInstance拆分为getClientInstance和getServerInstance(1.2.8.1)
  	 + Selector:增加getInstance，用于创建指定类型的实例(1.2.8.1)
  	 + Selector:增加DateFormatter实现(1.2.8.1)
  	 + Selector:增加Now实现(1.2.8.2)
  	 + AppProxyNormalizer:修正app取值中的/问题(1.2.8.2)
  	 + AbstractAccessController:AccessStat变更可见性为public(1.2.8.2)
  	 + IpAndServiceAccessController:修正配置变量的拼写错误(1.2.8.2)
  	 + ServantStat:不再收集缓冲池的信息(1.2.8.2)
  	 + ServantPool:扩展Reportable(1.2.8.2)
  	 + 对线程池和访问控制器体系进行Reportable和MetricsReportable改造(1.2.8.2)
  	 + 简化ServantStat模型(1.2.8.2)
  	 + 淘汰dbcp相关实现，将在下一个版本重写(1.2.8.2)
  	 
 - 1.2.9 [20141016 duanyy]
 	 + 增加基于JSON协议的REST远端调用框架(1.2.9)
 	 + 重写dbcp(1.2.9)
 	 + dbcp:增加ConnectionPool实现ModelledImpl(1.2.9.1)
 	 + remote:实现Reportable(1.2.9.1)
 	 + 软件包com.logicbus.manager被淘汰(1.2.9.1)
 	 + 软件包com.logicbus.selector整体迁出anyWebLoader项目(1.2.9.1)
 	 + ServantStat和ConnectionPoolStat采用Counter模型(1.2.9.1)
	 + 淘汰ChangeAware模型，转为更为通用的Watcher模型(1.2.9.1)
	 + 软件包com.logicbus.backend.stats调整到com.logicbus.backend.metrics(1.2.9.2)
	 + 软件包com.logicbus.client调整到com.logicbus.remote.client(1.2.9.2)
	 + remote:改造HttpCall，支持loadbalance(1.2.9.3)
	 + remote:改造Result,支持通过JsonPath提取对象(1.2.9.3)
	 + remote:增加simulate实现，用于模拟调用数据(1.2.9.3)
	 + dbcp:增加对读写分离的支持(1.2.9.3)

 - 1.3.0 [20141023 duanyy]
 	 + 转换为Maven工程(1.3.0.0)，并改名为alogic(1.3.0.0)	 