load
====
load用于从指定缓存中装入对象，并以指定的tag输出到当前文档节点。

load必须在某个cache语句之内，参见[cache](cache.md)。

### 实现模块

com.alogic.together.idu.CacheQuery

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | cacheConn | 上下文对象的id，缺省为cacheConn |
| 2 | tag | 输出对象的tag |
| 3 | id | 缓存对象的id |

### 案例

下面案例从用户缓存中装入用户id为alogic的对象。

```xml
	<script>
		<using xmlTag="idu" module="com.alogic.together.idu.IDU"/>
		<idu>
			<!--连接用户表缓存-->
			<cache cacheId="users">
				<!--在用户缓存中查找alogic用户-->
				<load tag="user" id="alogic"/>
			</cache>
		</idu>
	</script>
```


