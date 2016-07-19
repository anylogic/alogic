switch
======
switch用于条件控制语句。

switch是一个segment语句，其子语句是条件的各个分支，每个语句需要定义case值。

### 实现模块

com.alogic.xscript.plugins.Switch

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | value | 条件判断值，可计算 |

### 案例

现有下列脚本。

```xml
	<script>
		<check id="who"/>
		<switch value="${who}">
			<log case="crm" msg="who=crm"/>
			<log case="erp" msg="who=erp"/>
			<log case="default" msg="who=${who}"/>
		</switch>
	</script>
```

在上述脚本中，根据输入参数who的取值，来选择相应的路由。



