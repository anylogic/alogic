zk-setAsJson
=======

zk-setAsJason用于将当前节点数据以Json字符串形式设置到指定变量中。


### 实现类

com.alogic.xscript.zk.ZKGetAsJson

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | id | 变量id |


### 案例

```

	<?xml version="1.0"?>
	<script>
		<using xmlTag="zk-conn" module="com.alogic.xscript.zk.ZKConn" />
		
		<!-- 创建一个连接到本地ZooKeeper -->
		<zk-conn>
		
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
		</zk-conn>
	</script>

```

参考[ZooKeeper操作案例](Example.md)