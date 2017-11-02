zk-set
=======

zk-set用于设置指定路径的数据，即创建或更新指定节点数据。

>若指定路径存在，本指令对应ZooKeeper中的	setData(final String path, byte data[], int version)接口；
>若指定路径不存在，本指令对应ZooKeeper中的  create(final String path, byte data[], List<ACL> acl, CreateMode createMode)接口;

### 实现类

com.alogic.xscript.zk.ZKSetData

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | path | 指定的路径，可计算的 |
| 2 | mode | 创建模式，接收字符串:PERSISTENT; EPHEMERAL; PERSISTENT_SEQUENTIAL; EPHEMERAL_SEQUENTIAL，对应关系分别为：PERSISTENT; EPHEMERAL; PERSISTENT_SEQUENTIAL; EPHEMERAL_SEQUENTIAL |
| 3 | data | 设置的数据，可计算的 |
| 4 | ignoreException | 是否忽略异常，缺省值为false |

### 案例

```

	<?xml version="1.0"?>
	<script>
		<using xmlTag="zk-conn" module="com.alogic.xscript.zk.ZKConn" />
		
		<!-- 创建一个连接到本地ZooKeeper -->
		<zk-conn>
		
			<!-- 设置要存的数据变量 -->
			<set id="data" value="global-data" />
			
			<zk-set path="/test/global" data="${data}" mode="PERSISTENT" />	
			
			<!-- 查看结果 -->
			<zk-get path="/test/global" />
			<log mag="/test/global : ${$zk-get}" />
		
		</zk-conn>
	</script>

```

参考[ZooKeeper操作案例](Example.md)