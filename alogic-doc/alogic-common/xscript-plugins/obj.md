obj
===

obj创建一个新的对象(map),并输出到文档的当前节点。

obj是一个segment节点，支持子语句，子语句的当前节点将是obj所创建的节点。

### 实现模块

com.alogic.xscript.plugins.Obj

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | tag | 输出文档中的tag,缺省为data |

### 案例

现有下列脚本：
```xml
	<script>
	        <set id="array" value="tom;jerry;alogic;ketty"/>
	        <foreach in="${array}">
	                <obj tag="${$value}">
	                        <get id="name" value="${$value}"/>
	                </obj>
	        </foreach>
	</script>
```

脚本的输出文档为:
```
	{
	    "ketty": {
	        "name": "ketty"
	    },
	    "jerry": {
	        "name": "jerry"
	    },
	    "tom": {
	        "name": "tom"
	    },
	    "alogic": {
	        "name": "alogic"
	    }
	}
```
