constants
=========
constants用于设置一个常量值。

> 如果要设置一个可计算的变量，可以使用[set](set.md)


### 实现模块

com.alogic.xscript.plugins.Constants

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | id | 待设置的变量id |
| 2 | value | 该变量的值 |

### 案例

设置name变量为alogic.

```xml
	<constants id="name" value="alogic"/>
	<log msg="name=${name}"/>
```

上述脚本输出为
```
	name=alogic
```

