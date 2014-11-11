alogic-addons readme
====================

### 概述
alogic-addons是alogic附加的定制包，对alogic-core各项功能进行了具体化的定制，包括：

- com.logicbus.backend.acm,对alogic的访问控制模型进行定制,提供了ACM模型插件支持；

- com.logicbus.backend.metrics,对指标的处理进行了定制，提供了对外输出的插件

- com.logicbus.service，提供了dbcp、together、remote等模块的管理服务

### 版本
 - 1.3.0 [20141023 duanyy]
 	 + 初次发布，由anyLogicBus、anyFormula和anyWebLoader合并而来
 	 + 采用alogic-remote组件来实现metrics对外输出插件(1.3.0.1)
 	 + Services:增加KValueReport服务(1.3.0.2)
 	 + MetricsHandler:RemoteWriter和ServieWriter从server.ip变量中获取本机IP(1.3.0.3)