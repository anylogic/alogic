commit
======

commit用于主动提交数据库事务。

commit必须在某个db语句之内，参见[db](db.md)。

commit仅在db的autoCommit设置为false时才可以使用。


### 实现模块

com.alogic.together.idu.Commit

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | dbconn | 上下文对象的id，缺省为dbconn |

### 案例

通过下列方法使用commit.

```xml
	<script>
		<using xmlTag="idu" module="com.alogic.together.idu.IDU"/>
		<check id="userId"/>
		<idu>
			<!--数据库连接的autoCommit设置为false-->
			<db autoCommit="false">
				<!--执行数据库操作-->
				<delete sql.Delete="
					delete from user 
					where user_id = #{bind('userId')}
				"/>
				<delete sql.Delete="
					delete from user_passport 
					where user_id = #{bind('userId')}
				"/>	
				<!--如果没有异常，则commit-->
				<commit/>
				<!--如果有任何异常，则rollback-->
				<except id="except">
					<rollback/>
				</except>
			</db>
		</idu>
	</script>
```
