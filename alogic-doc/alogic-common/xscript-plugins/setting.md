setting
=======

setting用于设置一个变量，其变量值从Settings中提取。

### 实现模块

com.alogic.xscript.plugins.Setting

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
		<setting id="alogic" value="${alogic.home}"/>
        <log msg="alogic.home is ${alogic}"/>
	</script>
```
脚本的输出为:
```
	alogic.home is /Users/duanyy
```
