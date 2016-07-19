template
========
template解析一个预定义的JSON文档，并输出到文档的当前节点。

预定义的JSON文档，可以是一个字符串，也可以是一个文件。

当JSON文档的根节点是一个对象时，template是一个segment节点，支持子语句，子语句的当前节点将是template所创建的节点。
如果不是一个对象（数组，字符串之类），那么将忽略子语句的定义。

### 实现模块

com.alogic.xscript.plugins.Template

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | tag | 输出文档中的tag,缺省为data |
| 2 | content | 字符串形式的JSON文档 |
| 3 | src | JSON文档的文件位置, 当content为空时有效 |

### 案例

现有下列脚本：
```xml
	<script>
        <template tag="data1" content="{name3:alogic.alogic,name1:ketty,name2:alogic}">
        		<!--当前文档已切换，继续操作文档-->
            <get id="name4" value="ketty2"/>
        </template>
        <template tag="data2" src="file:///XX/XX/hello.json"/>
	</script>
```

脚本的输出文档为:
```
	{
	    "data1": {
	        "name3": "alogic.alogic",
	        "name4": "ketty2",
	        "name1": "ketty",
	        "name2": "alogic"
	    },
	    "data2": {
	        "hello": "helloworld"
	    }
	}
```

在上述案例中，hello.json文件的内容为:
```
	{hello:helloworld}
```


