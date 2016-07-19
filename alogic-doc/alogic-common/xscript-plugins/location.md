location
========
location用于切换文档的当前节点，通过jsonPath进行定位。

> 关于jsonPath，参加相关的网络资料:[JsonPath](https://github.com/jayway/JsonPath)
>
> 惊喜的发现，JsonPath现在迁移到了github.


location是一个segment节点，支持子语句，子语句的当前节点将是location所定位的节点，定位效果只在其子节点范围内有效。

### 实现模块

com.alogic.xscript.plugins.Location

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | path | 要切换的绝对路径 |

### 案例

现有下列脚本：
```xml
	<script>
		<!--通过template构建初始json-->
        <template tag="data" content="{}">
                <template tag="hello" content="{}"/>
                <template tag="hello1" content="{}"/>
        </template>
        <!--定位到$.data.hello-->
        <location path="$.data.hello">
                <get id="say" value="good bye"/>
        </location>		
	</script>
```
脚本的输出文档为:
```
	{
	    "data": {
	        "hello": {
	            "say": "good bye"
	        },
	        "hello1": {}
	    }
	}
```
