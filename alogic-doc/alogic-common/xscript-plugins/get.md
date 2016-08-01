get
===
get用于计算一个变量值，并输出到文档的当前节点。

### 历史

- 1.6.5.38 支持清除指定的对象，如果value的计算值为空，那么就是清除该对象；

### 实现模块

com.alogic.xscript.plugins.Get

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | id | 待输出的ID，为输出文档中的tag |
| 2 | value | 该变量的值,变量值可计算 |

### 案例

现有下列脚本：
```xml
	<script>
			<!--设置一个变量name-->
	        <set id="name" value="alogic"/>
	        
	        <!--输出到name1,可以是一个常量值-->
	        <get id="name1" value="ketty"/>
	        <!--输出到name2,可以是一个变量-->
	        <get id="name2" value="${name}"/>
			<!--输出到name3,可以是变量的计算结果-->
	        <get id="name3" value="${name}.${name}"/>
	</script>
```
脚本的输出文档为:
```
	{
	    "name3": "alogic.alogic", 
	    "name1": "ketty", 
	    "name2": "alogic"
	}
```
