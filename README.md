alogic readme
==================

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