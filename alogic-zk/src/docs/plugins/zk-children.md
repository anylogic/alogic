zk-children
=======

zk-children用于查询指定路径下子节点。

本指令可配合array和array-item使用，实现遍历子节点输出到当前文档的功能。

>本指令对应ZooKeeper中的List<String> getChildren(Path path,Watcher watcher,boolean ignoreException)接口

### 实现类

com.alogic.xscript.zk.ZKChildren

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | pid | 连接，缺省取$zk-conn |
| 2 | id | 操作返回变量id,缺省为$value |
| 3 | path | 指定的路径，可计算的 |
| 4 | ignoreException | 是否忽略异常，缺省值为false |
| 5 | offset | 设置偏移量，可计算的 |
| 6 | limt | 遍历数量，可计算的 |


### 案例
```

	<?xml version="1.0"?>
	<script>
		<using xmlTag="zk-conn" module="com.alogic.xscript.zk.ZKConn" />
		
		<!-- 创建一个连接到本地ZooKeeper -->
		<zk-conn>
		
			<zk-children path="test/global" offset="1" limit="5">
				
				<!-- 下面对遍历的子节点进行操作 -->
			
			<zk-children/>
		
		</zk-conn>
	</script>

```

参考[ZooKeeper操作案例](Example.md)