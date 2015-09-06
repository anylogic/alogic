# alogic-framework开发指南

alogic-framework提供了分布式Web服务开发的一整套方法和思路，解决了服务如何开发的问题。在alogic-framework下，一个成熟的Java开发者可以快速的开发出实现自己业务逻辑的Restful服务。

同时，它是一个web context，可以在任何一个标准Servlet容器（JETTY、Tomcat、WAS、JBOSS、WEBLOCIG等）中运行。

> alogic-framework当前采用开源的策略，任何开发者可以从[github](http://github.com)上下载使用，地址如下：[alogic-framework](https://github.com/yyduan/alogic)。

如果你是一个刚刚接触到服务开发的菜鸟选手，你可能需要了解一下在alogic下到底是如何开发服务的。

* [服务开发](alogic-framework/001.md)

当你已经深入的了解了服务开发之后，你会发觉你需要对框架下的一些要素进行个性化定制。

* [定制访问控制器](alogic-framework/002.md)
* [定制消息协议](alogic-framework/003.md)
* [定制输出日志及统计数据](alogic-framework/004.md)

这个时候，你可能还不是很了解如何构造自己的业务逻辑，不用担心，alogic提供了一些公共设施帮助你。

* [数据库连接池组件](alogic-framework/005.md)
* [KValue组件](alogic-framework/006.md)
* [远程调用组件](alogic-framework/007.md)
* [Cache组件](alogic-framework/008.md)
* [全局序列组件](alogic-framework/009.md)
* [定时小任务组件](alogic-framework/doer.md)


