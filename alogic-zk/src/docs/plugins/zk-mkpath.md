zk-mkpath
=======

zk-mkpath用于创建指定的路径。

>本指令对应ZooKeeper中的	create(String path, byte[] data, List<ACL> acl, CreateMode createMode)接口

### 实现类

com.alogic.xscript.zk.ZKMakePath

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | path | 指定的路径 |
| 2 | mode | 创建模式，接收字符串:PERSISTENT; EPHEMERAL; PERSISTENT_SEQUENTIAL; EPHEMERAL_SEQUENTIAL，对应关系分别为：PERSISTENT; EPHEMERAL; PERSISTENT_SEQUENTIAL; EPHEMERAL_SEQUENTIAL |



### 案例

```

	<?xml version="1.0"?>
	<script>
		<using xmlTag="zk-conn" module="com.alogic.xscript.zk.ZKConn" />
		
		<!-- 创建一个连接到本地ZooKeeper -->
		<zk-conn>
		
			<!-- 指定路径是否存在 -->
 			<zk-exist path = "/test/global/app-1" />
 			<log msg = "/test/global/app-1 exist? : ${$zk-exist} " />
 			
 			<!-- 创建路径 -->
 			<zk-mkpath path = "/test/global/app-1" mode="PERSISTENT"/>
 			<log msg = "make path : /test/global/app-1 ...... " />
 			
 			<!-- 看下创建结果 -->
 			<zk-exist path = "/test/global/app-1" />
 			<log msg = "/test/global/app-1 exist? : ${$zk-exist} " />
		
		</zk-conn>
	
	</script>

```


参考[ZooKeeper操作案例](Example.md)