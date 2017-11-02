zk-get
=======

zk-get用于获得指定的路径数据。

>本指令对应ZooKeeper中的byte[] 	getData(String path, Watcher watcher, Stat stat)接口

### 实现类

com.alogic.xscript.zk.ZKGetData

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | path | 指定的路径，可计算的 |
| 2 | ignoreException | 是否忽略异常,缺省值为false |
| 2 | id | 操作返回变量id,缺省为$zk-get |


### 案例

```

	<?xml version="1.0"?>
	<script>
		<using xmlTag="zk-conn" module="com.alogic.xscript.zk.ZKConn" />
		
		<!-- 创建一个连接到本地ZooKeeper -->
		<zk-conn>
		
			<!-- 判断路径test/global是否存在 -->
			<zk-get path="test/global" />
			<!-- 看看结果 -->
			<log msg="test/global : ${$zk-get}" />
			
		</zk-conn>
		
	</script>

```

参考[ZooKeeper操作案例](Example.md)