checkAndSet
===========

checkAndSet用来检查当前作用域是否存在指定的变量，如果为空，则设置缺省值。本语句通常用于检查前端是否输入想要的参数，并设置缺省值。

> 如果想抛出异常，可使用指令[check](check.md)


### 实现模块

com.alogic.xscript.plugins.CheckAndSet

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | id | 待检查的变量id,如果没有配置本参数，那么本语句无效 |
| 2 | dft | 当指定的变量不存在时，所设置的缺省值，取值支持变量 |

### 案例

检查参数name，如果不存在，设置为alogic
```xml
	<checkAndSet id="name" dft="alogic"/>
```

检查参数name，如果不存在，设置为appId的值
```xml
	<checkAndSet id="name" dft="${appId}"/>
```

