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

