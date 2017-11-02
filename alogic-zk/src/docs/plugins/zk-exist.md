zk-exist
=======

zk-exist用于判断指定的路径是否存在。

>本指令对应ZooKeeper中的stat exists(String path, Watcher watcher)接口

### 实现类

com.alogic.xscript.zk.ZKExist

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | path | 指定的路径，可计算的 |
| 2 | id | 操作返回变量id,缺省为$zk-exist |


### 案例

```

	<?xml version="1.0"?>
	<script>
		<using xmlTag="zk-conn" module="com.alogic.xscript.zk.ZKConn" />
		
		<!-- 创建一个连接到本地ZooKeeper -->
		<zk-conn>
		
			<!-- 判断路径test/global是否存在 -->
			<zk-exist path="test/global" />
			<!-- 看看结果 -->
			<log msg="path test/global exist? : ${$zk-exist}">
			
			
			<!-- 判断路径test/global是否存在 -->
			<zk-exist path="test/global" />
			<!-- 看看结果 -->
			<log msg="path test/global exist? : ${$zk-exist}" />
			
		</zk-conn>
		
	</script>

```

参考[ZooKeeper操作案例](Example.md)