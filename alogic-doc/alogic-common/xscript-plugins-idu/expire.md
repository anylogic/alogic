expire
======
expire用于将指定缓存中的指定对象过期。

load必须在某个cache语句之内，参见[cache](cache.md)。

### 实现模块

com.alogic.together.idu.CacheClear

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | cacheConn | 上下文对象的id，缺省为cacheConn |
| 3 | id | 缓存对象的id,支持变量计算 |

### 案例

下面案例将用户缓存中用户id为alogic的对象过期。

```xml
	<script>
		<using xmlTag="idu" module="com.alogic.together.idu.IDU"/>
		<idu>
			<!--连接用户表缓存-->
			<cache cacheId="users">
				<expire id="alogic"/>
			</cache>
		</idu>
	</script>
```


