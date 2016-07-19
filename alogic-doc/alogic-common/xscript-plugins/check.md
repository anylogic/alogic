check
=====

check用来检查当前作用域是否存在指定的变量，如果为空，则抛出相应的异常。本语句通常用于检查前端是否输入想要的参数。

> 如果不想抛出异常，而是设置一个缺省值，可使用指令[checkAndSet](checkAndSet.md)


### 实现模块

com.alogic.xscript.plugins.Check

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | id | 待检查的变量id,如果没有配置本参数，那么本语句无效 |
| 2 | code | 当指定的变量不存在时，所抛出的异常代码，缺省为client.args_not_found |
| 3 | reason | 当指定的变量不存在时，所抛出异常的错误原因，格式为String.format模版，缺省为Can not find parameters:%s |

### 案例

检查参数appId，如果不存在，抛出异常。
```xml
	<check id="appId" code="client.appId_not_found" reason="找不到应用id:%s"/>
```
