include
=======
include用于在当前代码段内引入另一个代码文件。

### 实现模块

com.alogic.xscript.plugins.Include

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | src | 另一端代码的位置 |


### 案例

现有脚本hello.xml，内容如下：
```
	<script>
		<get id="msg" value="hello world"/>
	</script>
```

现有下列脚本：
```xml
	<script>
        <template tag="data1" content="{}">
                <include src="file:///Users/duanyy/hello.xml"/>
        </template>
        <template tag="data2" content="{}">
                <include src="file:///Users/duanyy/hello.xml"/>
        </template>
	</script>
```

上述代码输出文档如下：
```
	{
	    "data1": {
	        "msg": "hello world"
	    }, 
	    "data2": {
	        "msg": "hello world"
	    }
	}
```
