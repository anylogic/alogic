getAsJson
=========

从变量值中解析Json对象，并输出到文档。

> getAsJson类似于[template](template.md),不同之处在于：
> - getAsJson不支持引入Json文件;
> - getAsJson的content支持计算;


### 历史

- 1.6.6.1 新增本插件;

### 实现模块

com.alogic.xscript.plugins.getAsJson

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | extend | 是否扩展对象，为true，则扩展文档的当前对象，为false，则以子对象追加到当前文档，缺省为false |
| 2 | tag | 以子对象追加到当前文档时的tag值，当extend为false有效 |
| 3 | content | 待解析为Json对象的Json文本内容，支持变量计算 |

### 案例

现有下列脚本：
```xml
	<script>
        <getAsJson extend="true" content="{name3:alogic.alogic,name1:ketty,name2:alogic}">
            <get id="name4" value="ketty2"/>
        </getAsJson>
	</script>
```

脚本的输出文档为:
```
	{
	    "name3": "alogic.alogic",
	    "name4": "ketty2",
	    "name1": "ketty",
	    "name2": "alogic"
	}
```
