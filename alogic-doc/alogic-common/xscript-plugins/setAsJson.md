setAsJson
=========

setAsJson用于设置一个变量，其取值是当前文档节点所转化的Json字符串。

### 历史

- 1.6.6.1 新增本插件;

### 实现模块

com.alogic.xscript.plugins.setAsJson

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | id | 上下文变量的id |

### 案例

现有下列脚本：
```xml

	<?xml version="1.0"?>
	<script>
		<obj tag="data">
			<get id="id" value="alogic"/>
			<get id="name" value="ketty"/>
			
			<setAsJson id="jsonData"/>
		</obj>
		
		<log msg="the json data is ${jsonData}"/>
	</script>

```

脚本的日志输出为:
```

	the json data is {"id":"alogic","name":"ketty"} 

```
