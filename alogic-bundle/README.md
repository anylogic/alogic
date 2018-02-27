alogic-bundle
=============

### Overview

alogic-bundle提供了一个更容易的方式来使用alogic框架，它包含alogic运行所需要的配置文件。使用者只需进行maven依赖，即可生成可用的war应用。

alogic-bundle所包含的配置文件如下：

| 文件 | 说明 |
| ---- | ---- |
| [ketty.web.xml](src/main/resources/bundle/ketty.web.xml) |  版本配置文件，定义了其它配置文件的位置 |
| [profile.xml](src/main/resources/bundle/profile.xml) | 场景配置文件，定义了一些缺省的配置 |
| [alogic.acm.xml](src/main/resources/bundle/alogic.acm.xml) | 缺省的访问控制配置文件，可通过环境变量acm.master重定义 |
| [alogic.bizlog.xml](src/main/resources/bundle/alogic.bizlog.xml) | 缺省的服务日志配置文件，可通过环境变量bizlog.master重定义 |
| [local.xml](src/main/resources/bundle/local.xml) | 缺省的部署配置文件,目前未定义任何配置 |
| [alogic.tlog.xml](src/main/resources/bundle/alogic.tlog.xml) | 缺省的trace日志配置文件，可通过trace.master重定义 |
| [alogic.metrics.xml](src/main/resources/bundle/alogic.metrics.xml) | 缺省的指标处理配置文件，可通过metrics.handler.client.master重定义 |
| [component.blob.xml](src/main/resources/bundle/component.blob.xml) | 缺省的blob配置文件，可通过blob.master重定义 |
| [component.sda.xml](src/main/resources/bundle/component.sda.xml) | 缺省的sda配置文件，可通过sda.master重定义 |
| [component.timer.xml](src/main/resources/bundle/component.timer.xml) | 缺省的定时器配置，可通过timer.master重定义 |
| [component.vfs.xml](src/main/resources/bundle/component.vfs.xml) | 缺省的定时器配置，可通过vfs.master重定义 |
| [alogic.web.xml](src/main/resources/bundle/alogic.web.xml) | 缺省的web.xml扩展文件，可通过webcontext.addons重定义 |
| [alogic.servant.config.xml](src/main/resources/bundle/alogic.servant.config.xml) | 缺省的服务目录配置文件，可通过servant.config.master重定义 |

### Getting started

按照下列步骤，可以轻松在您的项目中使用alogic框架。

> 可参考[alogic-bundle-demo](https://github.com/anylogic/alogic-bundle-demo)

#### 增加maven依赖

首先，你需要为你的项目增加maven依赖。

```xml

	<dependency>
	    <groupId>com.github.anylogic</groupId>
	    <artifactId>alogic-bundle</artifactId>
	    <version>1.6.11</version>
	</dependency>	

```

#### 修改web.xml

在您的web.xml中增加下列信息：

* 增加一个ContextListener.

```xml

	<listener>
		<listener-class>com.anysoft.webloader.WebAppMain</listener-class>
	</listener>

```

* 增加下列上下文参数：

```xml

	<context-param>
		<param-name>app.class</param-name>
		<param-value>com.alogic.bundle.Bootstrap</param-value>
	</context-param>
	
	<context-param>
		<param-name>ketty.web</param-name>
		<param-value>java:///bundle/ketty.web.xml#com.alogic.bundle.Bootstrap</param-value>
	</context-param>

	<context-param>
		<param-name>updater.auto</param-name>
		<param-value>false</param-value>
	</context-param>

	<context-param>
		<param-name>settings.master</param-name>
		<param-value>${master.home}/profile.xml#com.alogic.bundle.Bootstrap</param-value>
	</context-param>
	
	<context-param>
		<param-name>settings.secondary</param-name>
		<param-value>${secondary.home}/profile.xml#com.alogic.bundle.Bootstrap</param-value>
	</context-param>	

```

> 可参加alogic-bundle提供的一个模版[web.xml](src/main/resources/)

### 增加应用配置文件

在alogic-bundle的[ketty.web.xml](src/main/resources/bundle/ketty.web.xml)中指定了应用配置文件的位置：

```
	<parameter id="settings.ext.master" value="java:///conf/settings.xml#App" />
```

从该配置可以看出，它需要您的应用在CLASSPATH中提供一个settings.xml文件，同时该文件通过App类来进行引导。

首先，您需要创建一个引导类(注意，该引导类没有package):

```java

	/**
	 * 引导类
	 * 
	 * @author duanyy
	 *
	 */
	public class App {
	
	}
	
```

接着，您在conf目录下创建一个settings.xml，例如:

```

	<?xml version="1.0" encoding="UTF-8"?>
	<settings>
		<parameter id="server.app" value="alogic-war-demo"/>
		<parameter id="server.port" value="8080"/>
		
		<!-- 日志目录 -->
		<parameter id="ketty.logs.home" value="/Users/duanyy/tomcat" system="true"/>
		
		<!-- 服务主框架是否开启tracer -->
		<parameter id="servant.tracer" value="true"/>	
	</settings>

```

在settings中，您有必要设置下列变量：
* server.app 当前应用id
* server.port 当前实例的端口
* ketty.logs.home 日志所在的目录
* servant.tracer 是否开启trace日志

至此，您已经完成了alogic基本框架的引入，将您的工程编译为war，然后放到应用服务器（如tomcat等）中运行，你将会看到alogic内置的一系列的服务。

```

	http://<ip>:<port>/services

```






