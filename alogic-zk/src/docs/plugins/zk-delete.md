zk-delete
=======

zk-delete用于删除指定的路径。

>本指令对应ZooKeeper中的void delete(Path path,boolean ignoreException)接口

### 实现类

com.alogic.xscript.zk.ZKDelete

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | path | 指定的路径 |
| 2 | ignoreException | 是否忽略异常，缺省值为false |


### 案例

```

	<?xml version="1.0"?>
	<script>
		<using xmlTag="zk-conn" module="com.alogic.xscript.zk.ZKConn" />
		
		<!-- 创建一个连接到本地ZooKeeper -->
		<zk-conn>
		
		<!-- 删除路径test/global -->
		<zk-delete path="test/global" />
		
		</zk-conn>
	</script>

```

参考[ZooKeeper操作案例](Example.md)