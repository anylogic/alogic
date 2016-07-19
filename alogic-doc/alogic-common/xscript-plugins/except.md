except
======

except用于在当前segment定义一个异常处理器。

except是一个segment语句，但只支持一个子语句，作为异常处理逻辑。

### 实现模块

com.alogic.xscript.plugins.Except

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | id | 所处理的异常id | 

### 案例

现有下列脚本。

```xml
	<script>
		<throw id="no_argu" msg="parameter is not found."/>
		<except id="no_argu">
			<log msg="can not find who parameter."/>
		</except>
		<finally>
			<log msg="finally,must be executed."/>
		</finally>
	</script>
```

上述脚本的输出日志如下：
```
[160719151021,897] [INFO] [com.alogic.xscript.Logiclet] can not find who parameter.
[160719151021,897] [INFO] [com.alogic.xscript.Logiclet] finally,must be executed.
```



