hash
====

hash用于对指定的值取哈希值，并对指定的limit取余

### 实现模块

com.alogic.xscript.plugins.Hash

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | id | 待设置的变量id,缺省为$hash |
| 2 | value | 该变量的值,变量值可计算 |
| 3 | limit | limit值 |


### 案例

现有下列脚本：
```xml

	<script>
		<uuid/>
		<hash id="index" value="${$uuid}" limit="10"/>
		<log msg="uuid=${$uuid};index=${index}"/>				
	</script>

```
