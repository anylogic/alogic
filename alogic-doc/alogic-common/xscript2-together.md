基于xscript的together
=====================

### 什么是together?
Together是algoic提供的一个服务组装框架，当前版本是2.0。

> together1.0是一种面向协议的组装框架，而2.0则是基于xscript2.0的一种面向过程的框架。


### together的内置上下文

为了应对服务化的场景，together在xscript中内置了下列的上下文变量：

| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | $clientIp | String | 客户端ip | 调用服务的客户端ip,本ip可以通过X-Forwarded-For机制伪装 |
| 2 | $clientIpReal | String | 客户端真实ip | 调用服务的客户端真实ip | 
| 3 | $sn | String | 本次调用的全局序列号 | |
| 4 | $host | String | 本次服务的主机和端口 | 格式为ip:port | 
| 5 | $method | String | http方法 | GET,POST等 |
| 6 | $query | String | URL的Query字串 | |
| 7 | $uri | String | 服务请求的URL | |
| 8 | $path | String | 服务路径 | | 

为了验证上面的变量，编写下列服务脚本：

```xml
	<?xml version="1.0"?>
	<script>
	        <get id="clientIp" value="${$clientIp}"/>
	        <get id="clientIpReal" value="${$clientIpReal}"/>
	        <get id="sn" value="${$sn}"/>
	        <get id="host" value="${$host}"/>
	        <get id="method" value="${$method}"/>
	        <get id="query" value="${$query}"/>
	        <get id="uri" value="${$uri}"/>
	        <get id="path" value="${$path}"/>
	</script>	
```

注册到服务中，路径为/xscript/Hello，在浏览器输入地址：

```
	http://localhost:9000/services/xscript/Hello
```

结果如下:
```
	{
	    "duration": "0", 
	    "sn": "DKWCM1RG3A", 
	    "host": "0:0:0:0:0:0:0:1:9000", 
	    "reason": "It is successful", 
	    "query": "", 
	    "path": "/xscript/Hello", 
	    "method": "GET", 
	    "code": "core.ok", 
	    "clientIp": "0:0:0:0:0:0:0:1", 
	    "serial": "DKWCM1RG3A", 
	    "uri": "http://localhost:9000/services/xscript/Hello", 
	    "clientIpReal": "0:0:0:0:0:0:0:1"
	}	
```

### 注册together服务

当写好服务脚本之后，如何才能变成together服务呢？

众所周知，alogic对服务的注册依靠服务目录文件，例如：

```xml
	<?xml version="1.0" encoding="UTF-8"?>
	<catalogs>
		<!-- alogic-framework的内置服务 -->
		<catalog module="com.logicbus.models.servant.impl.XMLResourceServantCatalog"
			xrc="/com/logicbus/service/servant.addons.xml" class="com.logicbus.models.servant.impl.XMLResourceServantCatalog"
			name="alogic" />
	
		<!-- ketty的内置服务 -->
		<catalog module="com.logicbus.models.servant.impl.XMLResourceServantCatalog"
			xrc="/com/ketty/service/servant.xml" class="com.ketty.service.Agent"
			name="ketty" />
	</catalogs>	
```

在该文件中的每一个服务目录就是一个ServantCatalog的实现，together实现了2种实现。

#### LocalFileSystemServantCatalog
LocalFileSystemServantCatalog可以将一个本地文件系统的目录发布为服务目录，例如：

```xml
	<catalog module="com.alogic.together.catalog.LocalFileSystemServantCatalog" 
		home="D://temp"/>	
```
上面的配置将D:/temp目录发布为服务目录，如果该目录下存在下列服务脚本

```
	+ temp
	    + xscript
	        - Hello.xml
	        - Hello2.xml
	    + demo
	        - Test.xml
	        - Helloworld.xml
```

那么就发布了下列服务：

```
	/xscript/Hello
	/xscript/Hello2
	/demo/Test
	/demo/Helloworld
```

#### TogetherServantCatalog

TogetherServantCatalog可以将CLASSPATH中某个路径下的脚本文件发布为服务，例如：

```
	<catalog module="com.alogic.together.catalog.TogetherServantCatalog"
		xrc="/conf/servant-together.xml" class="App" />
```

由于CLASSPATH中不利于构建树形结构，所以TogetherServantCatalog依赖一个配置文件来构建目录，在上面案例中就是servant-together.xml。

再看看servant-together.xml.

```xml
	<catalog name="root">
	    <catalog name="xscript" home="/xscript" bootstrap="App">
	    <catalog name="demo" home="/demo" bootstrap="App">
	</catalog>
```

如果有下列文件的话:

```
	+ xscript
	    - Hello.xml
	    - Hello2.xml
	+ demo
	    - Test.xml
	    - Helloworld.xml
```

那么同样发布下列服务：

```
	/xscript/Hello
	/xscript/Hello2
	/demo/Test
	/demo/Helloworld
```






