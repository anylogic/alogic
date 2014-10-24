anyLogicBus系统部署文档
=======================

## 概述
anyLogicBus是一个高效的,可扩展的,轻量级服务框架.本文档记录了anyLogicBus的部署方法。

## 项目依赖
anyLogicBus作为一个webcontext部署在Java应用服务器上，例如tomcat,jetty等。

> tomcat下载地址: http://tomcat.apache.org/download-80.cgi
> 
> jetty下载地址: http://download.eclipse.org/jetty/

anyLogicBus依赖于多个开源项目,包括：

> anyFormula : https://github.com/yyduan/anyFormula
> 
> anyWebLoader : https://github.com/yyduan/anyWebLoader
> 
> log4j : http://logging.apache.org/log4j/2.x/download.html

## 单服务器部署

### 软件部署

anyLogicBus首先是一个服务框架，因此可以很好的工作在单服务器模式之下。我们需要准备一个Context。通常一个Context的目录结构如下：

    +WEB-INF
    +----lib
    +----classes
    +----web.xml

首先，拷贝所需的jar库文件到lib目录之下，包括:

    anyFormula-*.*.*.jar
    anyWebLoader-*.*.*.jar
    log4j-*.*.*.jar

然后，编辑web.xml，增加下列配置：

    <?xml version="1.0" encoding="ISO-8859-1"?>
    <web-app xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd" version="2.4">
        <description>logicbus server</description>
        <display-name>logicbus server</display-name>
        <servlet>
            <display-name>MessageRouter</display-name>
            <servlet-name>MessageRouter</servlet-name>
            <servlet-class>com.anysoft.webloader.ServletAgent</servlet-class>
	        <init-param>
	            <param-name>handler</param-name>
	            <param-value>com.logicbus.backend.server.http.MessageRouterServletHandler</param-value>
	        </init-param>
	        <load-on-startup>1</load-on-startup>
	     </servlet>
         <servlet-mapping>
             <servlet-name>MessageRouter</servlet-name>
             <url-pattern>/services/*</url-pattern>
         </servlet-mapping>
         <session-config>
             <session-timeout>30</session-timeout>
         </session-config>
         <welcome-file-list>
             <welcome-file>/index.html</welcome-file>
         </welcome-file-list>
         <listener>
             <listener-class>com.anysoft.webloader.WebAppContextListener</listener-class>
         </listener>
         <context-param>
             <param-name>app.class</param-name>
             <param-value>com.logicbus.backend.server.LogicBusApp</param-value>
         </context-param>
     </web-app>

上述web.xml配置遵守anyWebLoader的相关要求.

启动Jetty或tomcat服务.在浏览器察看服务列表:

    http://localhost:8080/logicbus/services
    
### 定制配置

缺省状态下，anyLogicBus只提供了几个基础的管理服务。接下来，可以通过配置来增加更多的功能。首先，可以规划三个基本变量，用来后续的配置：

 - local.home

本地home目录，用于存放一些本地的资源，例如本地库文件目录等。

> 实际上，anyWebLoader的本地缓存目录updater.home的缺省值正是基于local.home变量。
> updater.home=${local.home}/libs

 - master.home

资源根目录，只读，用于从远程或本地装入一些配置资源，支持java,http,file等。

 - secondary.home

资源备用根目录，作为master.home的备份。

#### 全局环境变量集

在缺省的配置下，全局环境变量文件的主备地址均指向为：

    settings.master=java:///com/logicbus/backend/server/http/profile.xml
    settings.secondary=java:///com/logicbus/backend/server/http/profile.xml

可以在web.xml中配置master.config和secondary.config变量指定自己的配置文件。例如：

    <context-param>
	    <param-name>settings.master</param-name>
	    <param-value>${master.home}/profile.xml</param-value>
	</context-param>
	<context-param>
	    <param-name>settings.secondary</param-name>
	    <param-value>${secondary.home}/profile.xml</param-value>
	</context-param>
	
全局环境变量文件可以定义一些全局变量，和在web.xml中定义context-param效果相同。缺省的profile.xml文件内容如下：

    <?xml version="1.0" encoding="GB2312"?>
    <settings>
        <parameter id="http.contenttype" value="text/xml;charset=utf-8"/>
        <parameter id="http.encoding" value="utf-8"/>
        <parameter id="servant.maxcount" value="10"/>
        <parameter id="acm.module" value="com.logicbus.backend.IpAndServiceAccessController"/>
    </settings>

可通过内置核心服务来查看当前所有的全局环境变量，如：

    http://[ip]:[port]/[mycontext]/services/core/manager/SettingsQuery

#### 定制访问控制器

访问控制器用于控制客户端对服务器访问的权限，定义了anyLogicBus调度框架访问控制的基本行为。可以通过全局环境变量来指定所采用的访问控制器。在web.xml或profile.xml中定义变量module.accesscontroller的取值，缺省值为{@link com.logicbus.backend.IpAndServiceAccessController com.logicbus.backend.IpAndServiceAccessController}。

anyLogicBus内置了下列几种访问控制器：

 - {@link com.logicbus.backend.IpAndServiceAccessController IpAndServiceAccessController} 对IP和服务限制并发数
 - {@link com.logicbus.backend.IpAccessController IpAccessController} 对IP限制并发数
 - {@link com.logicbus.backend.ServiceAccessController ServiceAccessController} 对服务限制并发数

当然，可以自己实现{@link com.logicbus.backend.AccessController AccessController}以实现更复杂的控制功能。

> anyLogicBus内置了查询服务查询AccessController的控制会话数据。
> http://[host]:[port]/[webcontext]/services/core/AclQuery

#### 定制定时器

anyLogicBus内置了一个定时器框架，可以通过配置文件启动定时器。缺省的配置文件位置为：

    timer.config.master=java:///com/logicbus/backend/timer/timer.xml
    timer.config.secondary=java:///com/logicbus/backend/timer/timer.xml
    
可以在web.xml或者profile.xml中配置master.timer.config和secondary.timer.config变量指向自己的配置文件。通常建议基于基础变量进行配置:

    timer.config.master=$(master.home)/timer.xml
    timer.config.secondary=${secondary.home}/timer.xml

一个典型的定时器配置文件如下：

    <?xml version="1.0" encoding="GB2312"?>
    <timers logListener="com.logicbus.backend.timer.DefaultLogListener">
	    <timer name="定期释放内存(每小时)" matcher="Crontab" crontab="00 * * * *" task="com.logicbus.backend.timer.GC"/>
	</timers>

#### 定制服务目录

服务目录是anyLogicBus核心模型。服务目录的缺省配置文件为：

    servant.config.master=java:///com/logicbus/models/servant/default.xml
    servant.config.secondary=java:///com/logicbus/models/servant/default.xml

可以在web.xml或者profile.xml中配置master.servant.config和secondary.servant.config指向自己的配置文件。通常建议基于基础变量配置：

    servant.config.master=$(master.home)/servant.xml
    servant.config.secondary=$(secondary.home)/servant.xml

缺省的服务目录配置文件如下：

    <?xml version="1.0" encoding="UTF-8"?>
    <catalogs>
        <catalog 
        module="com.logicbus.models.servant.impl.XMLResourceServantCatalog" 
        xrc="/com/logicbus/service/servant.xml"
        class="com.logicbus.models.servant.impl.XMLResourceServantCatalog"
        name="inner"
        />
    </catalogs>
    
可以在配置文件中配置一到多个目录，anyLogicBus在按照次序查找服务，如果两个服务目录包含同一个服务的定义的话，则取前一个。

服务目录的实现方式有多种，包括：

 - {@link com.logicbus.models.servant.impl.FileSystemServantCatalog FileSystemServantCatalog} 基于文件系统的Servant目录实现
 - {@link com.logicbus.models.servant.impl.XMLDocumentServantCatalog XMLDocumentServantCatalog} 基于XML文档的ServantCatalog实现
 - {@link com.logicbus.models.servant.impl.XMLResourceServantCatalog XMLResourceServantCatalog} 基于Java路径中XML文档的目录实现
 - {@link com.logicbus.models.servant.impl.MetadataServantCatalog MetadataServantCatalog} 基于元数据服务器的服务目录实现
 
当然，你也可以定制{@link com.logicbus.models.servant.ServantCatalog ServantCatalog}来实现自己的服务目录。

服务目录最为基本的元素是服务描述，服务通过XML来进行描述，服务目录的作用就是将这些XML组织起来。一个典型的服务描述文件为：

    <?xml version="1.0" encoding="GB2312"?>
    <service id="Helloworld2"
    name="Helloworld2"
    note="Helloworld ,我的第一个Logicbus服务。"
    visible="public"
    module="project.demo.service.Helloworld">
        <properties>
            <parameter id="welcome" value="北京欢迎你....."/>
        </properties>
        <modules>
            <module url="file:///D:/software/anyLogicBusDemo-v1.0.0.jar"/>
        </modules>
    </service>