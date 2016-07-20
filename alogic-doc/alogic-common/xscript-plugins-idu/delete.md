delete
======
delete用于执行delete语句。

本插件中所使用的SQL支持动态SQL语句，参考[sqlprocessor](../sqlprocessor.md)。

delete必须在某个db语句之内，参见[db](db.md)。

### 实现模块

com.alogic.together.idu.Delete

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | dbconn | 上下文对象的id，缺省为dbconn |
| 2 | sql.Delete | 删除SQL语句 |

### 案例

下面案例从数据库中删除指定用户.

```xml
	<script>
		<using xmlTag="idu" module="com.alogic.together.idu.IDU"/>
		<check id="userId"/>
		<idu>
			<db>
				<delete sql.Delete="
					delete from user 
					where user_id = #{bind('userId')}
				"/>
			</db>
		</idu>
	</script>
```


