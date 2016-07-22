array
=====

array创建一个新的数组(list),并输出到文档的当前节点。

array必须配合array-item使用,参考[array-item](array-item.md)。

### 实现模块

com.alogic.xscript.plugins.Array

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | tag | 输出文档中的tag,缺省为data |
| 2 | id | 输出上下文对象的id,缺省为$array |


### 案例

现有下列脚本：
```xml
	<script>
	        <set id="array" value="tom;jerry;alogic;ketty"/>
	        <array tag="user">
	                <foreach in="${array}">
	                        <array-item>
	                                <get id="id" value="${$value}"/>
	                        </array-item>
	                </foreach>
	        </array>
	</script>
```

脚本的输出文档为:
```
	{
	    "user": [
	        {
	            "id": "tom"
	        },
	        {
	            "id": "jerry"
	        },
	        {
	            "id": "alogic"
	        },
	        {
	            "id": "ketty"
	        }
	    ]
	}
```
