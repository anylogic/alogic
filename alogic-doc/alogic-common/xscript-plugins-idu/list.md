list
====
list用于在数据库中查询出多行记录，并以数组的形式输出到文档。

本插件中所使用的SQL支持动态SQL语句，参考[sqlprocessor](../sqlprocessor.md)。

list必须在某个db语句之内，参见[db](db.md)。

### 实现模块

com.alogic.together.idu.ListAll

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | dbconn | 上下文对象的id，缺省为dbconn |
| 2 | sql.Query | 查询SQL语句 |
| 3 | tag | 输出对象的tag，支持变量计算 | 

### 案例

下面案例从数据库中查询出用户列表。

```xml
	<script>
		<using xmlTag="idu" module="com.alogic.together.idu.IDU"/>
		<idu>
			<db>
				<list tag="users" sql.Query = "
					select 
						user_id userId,name,email,mobile 
					from 
						user
				"/>
			</db>
		</idu>
	</script>
```


