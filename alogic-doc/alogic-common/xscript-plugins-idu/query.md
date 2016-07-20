query
=====
query用于在数据库中查询出单行记录，并以对象的形式输出到文档。

本插件中所使用的SQL支持动态SQL语句，参考[sqlprocessor](../sqlprocessor.md)。

query必须在某个db语句之内，参见[db](db.md)。

### 实现模块

com.alogic.together.idu.Query

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | dbconn | 上下文对象的id，缺省为dbconn |
| 2 | sql.Query | 查询SQL语句 |
| 3 | tag | 输出对象的tag | 

### 案例

下面案例从数据库中查询出指定的用户。

```xml
	<script>
		<using xmlTag="idu" module="com.alogic.together.idu.IDU"/>
		<check id="userId"/>
		<idu>
			<db>
				<query tag="user" sql.Query = "
					select 
						user_id userId,name,email,mobile 
					from 
						user
					where 
						user_id = #{bind('userId')}
				"/>
			</db>
		</idu>
	</script>
```


