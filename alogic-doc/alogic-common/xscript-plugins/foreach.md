foreach
=======

foreach用于控制字符串数组的循环。


### 实现模块

com.alogic.xscript.plugins.ForEach

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | in | 用来循环的字符串数组，通常以某个分隔符进行分隔，支持变量计算 | 
| 2 | id | 循环体中变量的id,缺省为$value | 
| 3 | delimeter | 字符串数组的分隔符，缺省为; |

### 案例

现有下列脚本。

```xml
	<script>
		<set id="array" value="tom;jerry;alogic"/>
		<foreach in="${array}">
			<template tag="${$value}" content="{}">
				<get id="id" value="${$value}"/>
			</template>
		</foreach>
	</script>
```

上述脚本的输出文档如下：
```
	{
	    "ketty": {
	        "id": "ketty"
	    },
	    "jerry": {
	        "id": "jerry"
	    },
	    "tom": {
	        "id": "tom"
	    },
	    "alogic": {
	        "id": "alogic"
	    }
	}
```



