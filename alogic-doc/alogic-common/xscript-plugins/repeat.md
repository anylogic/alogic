repeat
======
repeat用于循环控制语句。

repeat必须根据文档中的数组来进行循环，数组的每个对象是循环子语句的当前处理节点，类似于angular中的ng-repeat.

### 实现模块

com.alogic.xscript.plugins.Repeat

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | path | 用来循环的对象列表的jsonPath | 

### 案例

现有下列脚本。

```xml
	<script>
		<!--通过template来初始化文档-->
		<template tag="data" content="[{},{},{},{},{},{}]"/>
		<!--针对data数组进行循环-->
		<repeat path="$.data[*]">
			<uuid id="rowId" length="10"/>
			<get id="id" value="${rowId}"/>
		</repeat>
	</script>
```

上述脚本的输出文档如下：
```
	{
	    "data": [
	        {
	            "id": "HqwPyPmHm4"
	        }, 
	        {
	            "id": "Ft52BvL451"
	        }, 
	        {
	            "id": "pfXd1lpKWp"
	        }, 
	        {
	            "id": "xQhwnSDPZg"
	        }, 
	        {
	            "id": "jq0jzsfe3f"
	        }, 
	        {
	            "id": "LfBjRPYEBy"
	        }
	    ]
	}	
```



