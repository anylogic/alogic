formula
=======
formula用于设置一个变量，其变量值是通过公式计算的。

> 本插件使用到了alogic的公式解析器，参见[公式解析器](../formulaparser.md)。


### 实现模块

com.alogic.xscript.plugins.Formula

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | id | 待设置的变量id |
| 2 | expr | 表达式，变量值通过该表达式计算 |

### 案例

现有下列脚本：
```xml
	<script>
		<formula id="name" expr="'alogic'"/>
		<log msg="name=${name}"/>
		<formula id="app" expr="'app.' + name"/>
		<log msg="app=${app}"/>
	</script>
```
脚本的输出为:
```
	name=alogic
	app=app.alogic
```
