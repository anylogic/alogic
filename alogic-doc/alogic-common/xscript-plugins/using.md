using
=====
using用来向当前的segment来注册插件。

### 实现模块

com.alogic.xscript.plugins.Using

### 配置参数

using支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | xmlTag | 指令的xml标记，插件在本脚本内部所使用的语句标记 |
| 2 | module | 插件的实现模块，一般是java实现类的类名 |

### 案例

如果有插件com.alogic.xscript.plugins.Helloworld，可以通过下列方式注册为hello语句

```xml
	<using xmlTag="hello" module="com.alogic.xscript.plugins.Helloworld"/>
```

那么在using所在的segment中，就可以多次使用hello语句.

```xml
	<hello/>
	<hello/>
```









