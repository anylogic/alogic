idu
===
idu用于创建一个namespace，所有在idu中注册了的插件，不需要再通过using引入。

可以在idu下直接使用的插件包括：


### 实现模块

com.alogic.together.idu.IDU

### 配置参数

无

### 案例

通过下列方法使用idu.

```xml
	<script>
		<using xmlTag="idu" module="com.alogic.together.idu.IDU"/>
		<idu>
			<!--下面使用idu注册插件进行操作-->
		</idu>
	</script>
```

