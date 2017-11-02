alogic-xscript-zk
=================

### Overview

alogic-xscript-zk是基于xscript2.0的zookeeper插件，提供了使用zk所需的相关指令，无缝对接zookeeper。


### Getting started

按照以下步骤，您可轻松在您的项目中使用alogic-xscript-zk.

不过开始之前，我们希望您了解xscript的相关知识。

- [xscript2.0](https://github.com/yyduan/alogic/blob/master/alogic-doc/alogic-common/xscript2.md) - 您可以了解xscript的基本原理及基本编程思路
- [xscript2.0基础插件](https://github.com/yyduan/alogic/blob/master/alogic-doc/alogic-common/xscript2-plugins.md) - 如何使用xscript的基础插件
- [基于xscript的together](https://github.com/yyduan/alogic/blob/master/alogic-doc/alogic-common/xscript2-together.md) - 如何将你的script发布为alogic服务

#### 增加maven依赖

您可以在中央仓库上找到[alogic-xscript-zk](http://mvnrepository.com/search?q=com.github.anylogic%3Aalogic-xscript-zk)的发布版本。

```xml

    <dependency>
        <groupId>com.github.anylogic</groupId>
        <artifactId>alogic-xscript-zk</artifactId>
        <version>3.4.6-20160825</version>
    </dependency>   	

```

> alogic-xscript-zk版本号前面的3.4.6是其所依赖的ZooKeeper客户端的版本号，后面的20160825是其发布的日期。

#### 引入Namespace

在您的脚本中，你需要引入ZKConn作为Namespace，比如:

```xml
	
	<using xmlTag="zk-conn" module="com.alogic.xscript.zk.ZKConn"/>
	
	<zk-conn>
		<!--
			在这里你可以使用alogic-xcript-zk提供的语句
		-->
	</zk-conn>
```

### Example

下面的案例是对本地ZooKeeper的基本操作。

```xml

	<?xml version="1.0"?>
	<script>
		<using xmlTag="zk-conn" module="com.alogic.xscript.zk.ZKConn" />
	
		<!-- 创建一个连接到本地ZooKeeper -->
		<zk-conn >
			
			<!-- 指定路径是否存在 -->
			<zk-exist path = "/test/global/app-1" />
			<log msg = "/test/global/app-1 exist? : ${$zk-exist} " />
			<!-- 创建路径 -->
			<zk-mkpath path = "/test/global/app-1" />
			<log msg = "make path : /test/global/app-1 ...... " />
			<zk-mkpath path = "/test/global/app-2" />
			<log msg = "make path : /test/global/app-2 ...... " />
			<zk-mkpath path = "/test/global/app-3" />
			<log msg = "make path : /test/global/app-3 ...... " />
			
			<!-- 看下创建结果 -->
			<zk-exist path = "/test/global/app-1" />
			<log msg = "/test/global/app-1 exist? : ${$zk-exist} " />
			
			<!-- 分别为每个子路径设置数据 -->
			<zk-children path = "/test/global">
				<!-- 组建Json数据 -->
				<obj tag="data">
					<get id="id" value="alogic"/>
					<get id="name" value="ketty"/>
					<get id="znode" value="${$value}" />
					<!-- 当前节点数据转为Json字符串存入指定变量 -->
					<zk-setAsJson id="jsonData"/>
					<!-- 设置指定路径数据 -->
					<zk-set path = "/test/global/${$value}" data = "${jsonData}" />			
				</obj>
			</zk-children>		
			
			<!-- 遍历指定路径的子路径输出到当前文档 -->
			<array tag="instance">
				<zk-children path="/test/global">
		               <array-item>
		               		<get id="id" value="${$value}"/>
		               		<zk-get id="data" path="/test/global/${$value}"/>
		               		<zk-getAsJson tag="data" content="${data}" extend="true"/>
		               </array-item>
				</zk-children>
			 </array>
			
			<!-- 删除指定路径 -->
			<zk-delete path = "/test" />
	
		</zk-conn>
	
	</script>
```

为正确执行上述指令，需要在本地安装好ZooKeeper并启动，并且在[settings.xml](src/test/resources/settings.xml)中配置zk的位置：

```xml

	<parameter id="zookeeper.connectString" value="127.0.0.1:2181"/>

```

ZooKeeper启动后，就可以运行[demo](src/test/java/Demo.java)来测试xscript脚本。

### Reference

参见[alogic-xscript-zk参考](src/docs/reference.md)。

### History
    
- 3.4.6 [20160809 duanyy]
	+ 初次发布
- 3.4.6.1 [20160811 lijun]
	+ 补充了包com.alogic.xscript.zk内的ZooKeeper基本操作，并完成相关文档编写
- 3.4.6.2 [20160823 duanyy]
	+ 修正代码，增加zk-setAsJson,zk-getAsJson
- 3.4.6.3 [20160824 lijun]
	+ 修改测试脚本，修改相应文档
- 3.4.6.4 [20160825 duanyy]
	+ 发布20160825版本。
- 3.4.6.5 [20170303 duanyy]
	+ ZookeeperConnector的Path修改为UPath
- 3.4.6.6 [20170512 duanyy]
	+ 基于alogic-1.6.8发布新版本
	+ 解决1.6.8中xscript的兼容性问题
- 3.4.6.7 [20170516 duanyy]
	+ 优化ZKGetAsJson插件
		
	

	
