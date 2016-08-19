alogic readme
=============

一个高效的轻量级服务框架

### 特征

alogic是一个高效的,可扩展的,轻量级服务框架,具有下列特征:

 - 服务路由，将HTTP请求分发给具体服务模块；

 - 访问控制，支持多种访问控制策略，并且可定制；

 - 插件式服务模块，提供服务规范，支持定制开发

 - 可定制的服务目录，支持多种形式的服务目录

 - 内置多种管理服务

 - 可部署成集群管理模式，分为元数据服务器，监控服务器，服务节点进行部署

 - 基于anyWebLoader自动更新框架

 - 面向协议的服务组装和并发调度框架,从1.1.0起提供
 
alogic已经在maven中央仓库进行了发布，参见[mvnrepository](http://mvnrepository.com/artifact/com.github.anylogic)。

### 版本
 - 1.3.0 [20141023 duanyy]
 	 + 初次发布，由anyLogicBus、anyFormula和anyWebLoader合并而来
 	 + 子目录alogic-ketty改名为alogic-addons(1.3.0.1)
	 + 将maven工程的groupId改为com.github.alogic(1.3.0.2)

 - 1.6.1 [20141117 duanyy]
 	 + 创建SNAPSHOT版本，自此之后，所有的子项目统一版本。
 	 + alogic-core:MessageDoc暴露InputStream和OutputStream(1.6.1.1)
 	 + alogic-core:增加Message实现ByteMessage(1.6.1.1)
 	 + alogic-common:Properties实现DataProvider(1.6.1.1)
 	 + alogic-core:增加批处理入口框架(1.6.1.1)
 	 + alogic-core:HttpContext增加Form数据拦截功能(1.6.1.2)
 	 + alogic-common:批处理入口框架增加对URL配置文件的支持(1.6.1.3)
 	 + alogic-common:在装入include文件时，通过loadable变量检测是否装入(1.6.1.4)
 	 + alogic-common:增加CopyRight类(1.6.1.5)

 - 1.6.2 [20141218 duanyy]
 	 + alogic-core:支持Comet技术(Comet需要针对Jetty服务器定制HttpContext插件，不包含在alogic中)(1.6.2.1)
	 + alogic-example:增加Comet演示服务(1.6.2.1)
	 + alogic-common:设置缺省ClassLoader为Thread.currentThread().getContextClassLoader()(1.6.2.2)
	 + alogic-dbcp:优化Select的API(1.6.2.3)
	 + alogic-dbcp:增加RowRenderer支持(1.6.2.4)
	 + alogic-core:增加CodeImage消息实现，用于验证码图片服务(1.6.2.5)
	 + alogic-core:优化会话管理机制(1.6.2.6)
	 + alogic:进入1.6.3版本

 - 1.6.3 [20150206 duanyy]
 	 + 创建1.6.3-SNAPSHOT版本，开始1.6.3版本开发
 	 + alogic-common:cache包仅仅考虑cache的读，淘汰datastore实现(1.6.3.1)
 	 + alogic-common:接口Cacheable增加了Cacheable.expire方法(1.6.3.2)
 	 + alogic-cache:新增cache框架及相应的管理服务(1.6.3.3)
 	 + alogic-common:淘汰Provider.load方法(1.6.3.3)
 	 + alogic-doer:增加小任务批处理框架(1.6.3.4)
 	 + alogic-common:DefaultProperties增加Json及XML的输出功能(1.6.3.4)
 	 + alogic-seq:增加sequeue框架(1.6.3.5)
 	 + alogic-seq:修正bug(1.6.3.6)
 	 + alogic-doer:客户端增加获取任务报告功能(1.6.3.6)
 	 + alogic-common:缺省的classLoader可以配置(1.6.3.7)
 	 + alogic-common:提升XMLResourceSimpleModelProvider的XML配置文件搜索性能(1.6.3.8)
 	 + alogic-core:修正NothingAccessController无法实例化问题(1.6.3.9)
 	 + alogic-core:增加忽略本次服务处理输出的功能(1.6.3.10)
 	 + alogic-dbcp:ConnectionPool增加recycle(Connection, boolean),获取客户的使用反馈,以便连接池的处理 (1.6.3.11)
 	 + alogic-dbcp:将所管理的Connection改变为ManagedConnection，以便支持读写分离(1.6.3.11)
 	 + alogic-core:直接通过HttpServletRequest获取当前的Session(1.6.3.12)
	 + alogic-dbcp:增加对Connection的有效性判断(1.6.3.13)
	 + alogic-core:修正formContentType所取的参数名问题，笔误(1.6.3.14)
	 + alogic-dbcp:修正Connection.isValid出异常的问题(1.6.3.15)
	 + alogic-core:修正备用bizlog.secondary变量名，笔误(1.6.3.16)
	 + alogic-dbcp:增加控制属性timeout(1.6.3.17)
	 + alogic-core:优化AccessController的虚基类(1.6.3.18)
	 + alogic-common:Main调整commands和includes处理次序，以便command参数能读取includes文件中的变量(1.6.3.19)
	 + alogic-common:Main对于某些环境变量，设置到System的Properties中(1.6.3.20)
	 + alogic-remote:增加全局序列号的支持(1.6.3.21)
	 + alogic-common:增加xscript脚本支持(1.6.3.22)
	 + algoic-common:上传xscript文档，参见[xscript脚本介绍](alogic-common/xscript.md)(1.6.3.22)
	 + alogic-common:优化xscript的编译方式(1.6.3.23)
	 + alogic-addons:增加多key的SQL语句Provider(1.6.3.24)
	 + alogic-cache:MultiFieldObject.Default增加构造函数(1.6.3.24)
	 + alogic-common:优化xscript的日志机制(1.6.3.25)
	 + alogic-doer:增加xscript的执行插件(1.6.3.26)
	 + alogic-core:核心服务增加XML和JSON双协议支持(1.6.3.27)
	 + alogic-blob:增加alogic-blob框架，用于存储和查询Blob数据(1.6.3.28)
	 + alogic-common:增加xscript的include语句插件(1.6.3.29)
	 + alogic-dbcp:增加一些db操作工具(1.6.3.30)
	 + alogic-dbcp:增加SQL预处理器(1.6.3.30)
	 + alogic-core:增加消息协议MultiPartForm，支持Http文件上传(1.6.3.31)
	 + alogic-blob:Blob增加MD5码，ContentType等元信息(1.6.3.32)
	 + alogic-blob:增加Blob文件上传功能(1.6.3.33)
	 + alogic-core:服务Simulator增加XML和JSON双协议支持(1.6.3.34)
	 + alogic-dbcp:增加密文形式的密码(1.6.3.35)
	 + alogic-main:xscript增加命令行入口(1.6.3.36)
	 + alogic-doer:增加定时器包(1.6.3.37)
	 + alogic-core:淘汰timer框架(1.6.3.37)
	 + alogic-doc:增加SQL预处理器文档(1.6.3.37)
	 + alogic-doer:增加相应的管理服务(1.6.3.37)
	 + alogic-doer:增加scheduler的集群功能(1.6.3.38)
	 + alogic-common:增加公式解析器文档,参见[公式解析器](alogic-common/formulaparser.md)(1.6.3.39)
	 + alogic-common:修正公式解析器的bug(1.6.3.39)
	 + alogic-doer:增加Monthly,Daily,Weekly,Hourly等日期匹配器实现(1.6.3.40)
	 + alogic-common:增加属性格式化功能(1.6.3.41)
	 + alogic:正式发布1.6.3版本。
 - 1.6.4 [20150825 duanyy]
	 + 创建1.6.4-SNAPSHOT版本，开始1.6.4版本开发
	 + alogic-lucene:增加搜索引擎模块(1.6.4.1)
	 + alogic-blob:BlobInfo增加length(1.6.4.2)
	 + alogic-cache:增加查询缓存对象的服务(1.6.4.3)
	 + alogic-addons:调整内置服务列表，并增加内置服务(1.6.4.4)
	 + alogic:补充文档,修正bug(1.6.4.5)
	 + alogic-addons:增加IDU实现，提供了基于数据库的简单的Insert Delete Update方案(1.6.4.6)
	 + alogic-blob:增加文件id的扫描功能(1.6.4.7)
	 + alogic-core:CORS成了可选配置(1.6.4.8)
	 + alogic-cache:缓存接口增加set方法(1.6.4.9)
	 + alogic-cache:增加NullProvider(1.6.4.9)
	 + alogic-core:所有日期输出不再格式化，由前端解决(1.6.4.10)
	 + alogic-core:日志类型为none的服务日志也将输出到bizlog(1.6.4.11)
	 + alogic-common:增加环形数据模型(RRM)模块(1.6.4.11)
	 + alogic-common:修正RRM的bug(1.6.4.12)
	 + alogic-common:修正RRM的bug(1.6.4.13)
	 + alogic-common:RRModelManager增加列表接口(1.6.4.14)
	 + alogic-kvalue:删除部分测试代码(1.6.4.15)
	 + alogic-addons:修正seq内置服务的配置问题(1.6.4.16)
	 + alogic:根据sonar建议优化代码(1.6.4.16)
	 + alogic:根据sonar建议优化代码(1.6.4.17)
	 + alogic-blob：增加自动图标集(1.6.4.18)
	 + alogic:根据sonar建议优化代码(1.6.4.19)
	 + alogic:根据sonar建议优化代码(1.6.4.20)
	 + alogic:根据sonar建议优化代码(1.6.4.21)
	 + alogic-core:HttpContext,当发生错误时，细化错误信息的输出(1.6.4.22)
	 + alogic-cache:扩展缓存模型(1.6.4.23)
	 + alogic-cache:修改权限模型(1.6.4.23)
	 + alogic-common:web.addons文件中增加对SessionListener的支持(1.6.4.24)
	 + alogic-common:修正公式解析器的死循环问题(1.6.4.25)
	 + alogic-blob:增加缺省的图标资源(1.6.4.26)
	 + alogic:根据sonar建议优化代码(1.6.4.27)
	 + alogic-core:增加基于会话的访问控制器实现(1.6.4.28)
	 + alogic-cache:增加基于缓存的会话管理器实现(1.6.4.28)
	 + alogic-cache:增加基于KValue的缓存实现(1.6.4.28)
	 + alogic-common:修正取构造器时的异常(1.6.4.29)
	 + alogic-core:清除Servant体系中处于deprecated的方法(1.6.4.29)
	 + alogic-core:指标序列化的间隔符修改为$(1.6.4.29)
	 + alogic-core:文件上传消息处理透传Context对象(1.6.4.30)
	 + alogic:改造计数器体系(1.6.4.31)
	 + alogic-common:增加RSA加密/解密基础类库(1.6.4.32)
	 + alogic-common:根据sonar建议优化代码(1.6.4.33)
	 + alogic-common:xscript完善日志输出(1.6.4.34)
	 + alogic-doer:增加LinkedScriptDoer(1.6.4.34)
	 + alogic-common:AbstractStatement调整activity的取值(1.6.4.35)
	 + alogic-core:AccessController实现XMLConfigurable和Configurable接口(1.6.4.35)
	 + alogic-core:增加ketty.web.xml文件，用于替代web.xml中的部分内容(1.6.4.36)
	 + alogic-doer:优化锁被打断的时的处理(1.6.4.37)
	 + alogic-core:优化WEB退出时的清理工作(1.6.4.38)
	 + alogic-doer:采用concurrent包来调度定时器(1.6.4.38)
	 + alogic-vfs:增加vfs模块(1.6.4.38)
	 + alogic-doer:scheduler可以作为一个一次性的timer运行 (1.6.4.39)
	 + alogic-vfs:文件信息增加属性path(1.6.4.40)
	 + alogic-common:增加XMLElement节点的属性操作工具(1.6.4.41)
	 + alogic-core:AclQuery增加分页功能(1.6.4.41)
	 + alogic-doer:Scheduler增加Linked实现(1.6.4.42)
	 + alogic-common:rrm直接对接指标处理器(1.6.4.42)
	 + alogic-common:KeyGen增加uuid生成功能(1.6.4.43)
	 + alogic-common:DataProvider增加获取原始值的接口(1.6.4.43)
	 + alogic-core:LogicBusApp在销毁的时候flush bizLogger和metricsHandler(1.6.4.44)
	 + alogic-core:Context增加toString()以便输出协议内容(1.6.4.44)
	 + alogic-common:AbstractHandler修正Report输出的bug，并增加统计数据的分页功能(1.6.4.44)
	 + alogic-common:指标量度Measures支持多个方法(1.6.4.45)
	 + alogic-dore:修正定时器有效期的bug(1.6.4.45)
	 + alogic-core:ServantCatalog实现Configurable和XMLConfigurable(1.6.4.46)
	 + 1.6.4 封版，发布release版本(1.6.4)
 - 1.6.5 [20150427 duanyy]
 	 + 创建1.6.5-SNAPSHOT版本，开始1.6.5版本开发
 	 + alogic-common:xscript语句中的activity可以通过变量计算(1.6.5.1)
 	 + alogic-common:修正xscript中activity取值的问题(1.6.5.2)
 	 + alogic-common:增加trace框架，提供方法级别的trace日志输出(1.6.5.3)
 	 + alogic-common:配置文件中的变量可以写入SystemProperties中(1.6.5.4)
 	 + alogic-core:AccessController增加reload接口(1.6.5.5)
 	 + alogic-addons:AccessControlModel增加key和verifier配置(1.6.5.5)
 	 + alogic-core:Context增加调用次序(1.6.5.6)
 	 + alogic-core:Context增加报文长度(1.6.5.6)
 	 + alogic-core:淘汰MessageDoc(1.6.5.6)
 	 + alogic-core:核心框架增加tracelog(1.6.5.6)
 	 + alogic-common:Handler增加采样功能(1.6.5.6)
 	 + alogic-common:Tracer可以选择关闭(1.6.5.7)
 	 + alogic-dbcp:考虑到其它数据库的需求，Preprocessor支持自定义绑定变量名(1.6.5.8)
 	 + alogic-addons:Proxy支持web应用的Context路径(1.6.5.8)
 	 + alogic-core:Context不再提供数据库Connection上下文(1.6.5.9)
 	 + alogic-together:淘汰alogic-together，准备推出新版本(1.6.5.9)
 	 + alogic-together:增加全新的服务组装together框架(1.6.5.10)
 	 + alogic-addons:修正采用HttpURLConnection导致的一些bug(1.6.5.11)
 	 + alogic-common:tracelog增加type字段(1.6.5.11)
 	 + alogic-common:增加load框架(1.6.5.12)
 	 + alogic-addons:增加together的idu操作插件(1.6.5.12)
 	 + alogic-common:load框架增加对象过期判断功能(1.6.5.13)
 	 + alogic-together:增加Encrypt和Decrypt插件(1.6.5.13)
 	 + alogic-together:增加repeat和switch插件(1.6.5.14)
 	 + alogic-together:调整db插件到addons(1.6.5.14)
 	 + alogic-together:增加uuid,now,formula等内置插件(1.6.5.15)
 	 + alogic-together:整合together脚本和xscript脚本,新增com.alogic.xscript包(1.6.5.16)
 	 + alogic-common:淘汰 xscript1.0（1.6.5.17)
 	 + alogic-common:优化内置的xscript插件(1.6.5.18)
 	 + alogic-together:增加基于本地文件系统的服务目录实现(1.6.5.18)
 	 + alogic-blob:增加project,avatar内置图标集(1.6.5.19)
 	 + alogic-core:服务器启动和关闭时可触发脚本执行(1.6.5.20)
 	 + alogic-vfs:增加sftp模式的实现(1.6.5.21)
 	 + alogic-vfs:增加基于vfs的目录比较工具(1.6.5.22)
 	 + alogic-common:xscript的switch插件不再使用selector(1.6.5.23)
 	 + alogic-together:增加服务环境下xscript的内置变量(1.6.5.24)
 	 + alogic-common:xscript增加include(1.6.5.25)
 	 + alogic-common:constants增加设置变量模版功能(1.6.5.26)
 	 + alogic-addons:修改idu中获取缓存对象操作为load(1.6.5.27)
 	 + alogic-common:xscript的template支持非Map类型(1.6.5.28)
 	 + alogic-addons:修正xscript中load的bug(1.6.5.29)
 	 + alogic-addons:xscript的IDU增加事务支持(1.6.5.30)
 	 + alogic-together:上下文增加变量$service(1.6.5.31)
 	 + alogic-cache:MultiFieldObject增加set相关接口(1.6.5.31)
 	 + alogic-cache:修正set接口的bug(1.6.5.32)
 	 + alogic-common:xscript增加foreach,obj,array,array-item等指令(1.6.5.33)
 	 + alogic-common:xscript增加trim,substr,lowercase,uppercase等指令(1.6.5.34)
 	 + alogic-addons:idu的load和query插件增加extend参数，用于扩展当前节点对象(1.6.5.35)
 	 + alogic-doer:优化timer的清理工作(1.6.5.36)
 	 + alogic-doer:timer提供刷新配置的功能(1.6.5.37)
 	 + alogic-common:xscript的repeat支持非map对象的循环(1.6.5.38)
 	 + alogic-common:xscript的get支持清除对象(1.6.5.38)
 	 + alogic-common:增加setting和env，分别从Settings和Envs中获取指定的值(1.6.5.39)
 	 + alogic-kvalue:HastRow增加getAll接口(1.6.5.40)
 	 + 1.6.5封版，发布release版本(1.6.5)
 - 1.6.6 [20160819 duanyy]
 	 + 创建1.6.6-SNAPSHOT版本,开始1.6.6版本开发.
 	 