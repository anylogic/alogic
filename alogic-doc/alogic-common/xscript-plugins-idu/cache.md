cache
=====
cache用于获取一个缓存连接。

cache是一个segment，支持子语句，所有子语句可以通过上下文对象使用该连接。该cache连接也只在子语句范围内有效。

当需要同时连接多个缓存的时候，可以通过cacheConn设置不同的上下文对象id.

### 实现模块

com.alogic.together.idu.Cache

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | cacheId | 要连接的缓存id |
| 2 | cacheConn | 上下文对象的id，缺省为cacheConn |

### 案例

通过下列方法使用cache.

```xml
	<script>
		<using xmlTag="idu" module="com.alogic.together.idu.IDU"/>
		<idu>
			<!--连接用户表缓存-->
			<cache cacheId="users">
				<!--在用户缓存中查找alogic用户-->
				<load tag="user" id="alogic"/>
				<!--连接用户角色列表缓存-->
				<cache cacheId="userroles" cacheConn="userroles">
					<!--查找alogic用户的角色列表-->
					<load tag="userroles" id="alogic" cacheConn="userroles"/>
				</cache>
			</cache>
		</idu>
	</script>
```


