set
===
set用于设置一个变量，其变量值是可以计算的。

### 实现模块

com.alogic.xscript.plugins.Set

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | id | 待设置的变量id |
| 2 | value | 该变量的值,变量值可计算 |

### 案例

现有下列脚本：
```xml
	<script>
		<!--设置name的值为-->
		<set id="name" value="alogic"/>
		<log msg="name=${name}"/>
		
		<!--设置app的值-->
		<set id="app" value="app.${name}"/>
		<log msg="app=${app}"/>
	</script>
```
脚本的输出为:
```
	name=alogic
	app=app.alogic
```
