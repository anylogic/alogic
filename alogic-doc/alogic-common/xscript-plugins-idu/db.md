db
==
db用于获取一个jdbc连接。

db是一个segment，支持子语句，所有子语句将可以通过上下文对象使用该连接，该数据库连接也只在子语句范围内有效。

当需要同时连接多个数据库的时候，可以通过dbconn设置不同的上下文对象id.

### 实现模块

com.alogic.together.idu.DBConnection

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | dbcpId | 要连接的数据源id |
| 2 | dbconn | 上下文对象的id，缺省为dbconn |

### 案例

通过下列方法使用db.

```xml
	<script>
		<using xmlTag="idu" module="com.alogic.together.idu.IDU"/>
		<idu>
			<!--连接数据库db1-->
			<db dbcpId="db1">
				<!--在db1中执行查询语句-->
				<query sql.Query=""/>
				<!--连接数据库db2-->
				<db dbconn="other" dbcpId="db2">
					<!--在db1中执行查询语句-->
					<query sql.Query=""/>
					<!--在db2中执行查询语句-->
					<query dbconn="other" sql.Query=""/>
				</db>
			</db>
			<!--连接数据库db3-->
			<db dbcpId="db3">
				<!--在db3中执行查询语句-->
				<query sql.Query=""/>
			</db>			
		</idu>
	</script>
```


