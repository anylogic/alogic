alogic-core readme
==================

alogic的核心框架
 
### 版本
 - 1.3.0 [20141023 duanyy]
 	 + 初次发布，由anyLogicBus、anyFormula和anyWebLoader合并而来
 	 + MessageRouter:当所访问的服务不存在时，以一个统一的服务名(/core/Null)来进行日志记录(1.3.0.1)
 	 + LogicBusApp:增加全局配置文件，变量名为settings.global.master和settings.global.secondary(1.3.0.2)
 	 + 解决问题：框架截获了post方式的body数据，导致post过来的form数据无法获取(1.3.0.2)
 	 + 修正bug:服务统计的统计口径问题(1.3.0.3)
 	 
 - 1.4.0 [20141117 duanyy]
 	 + 对服务调用的MessageDoc和Context模型进行改造
 	 + 修正部分javadoc警告