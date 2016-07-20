update
======
update用于执行update语句。

本插件中所使用的SQL支持动态SQL语句，参考[sqlprocessor](../sqlprocessor.md)。

update必须在某个db语句之内，参见[db](db.md)。

### 实现模块

com.alogic.together.idu.Update

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | dbconn | 上下文对象的id，缺省为dbconn |
| 2 | sql.Update | 更新SQL语句 |

### 案例

下面案例从数据库中更新用户信息。

```xml
	<script>
		<using xmlTag="idu" module="com.alogic.together.idu.IDU"/>
		<check id="userId"/>
		<idu>
			<db>
				<update sql.Update="
					update user 
					set 
						user_id = #{bind('userId')}
						#not_nvl(name,',name=' + bind('name'))
						#not_nvl(email,',email=' + bind('email'))
						#not_nvl(mobile,',mobile=' + bind('mobile'))
					where 
					user_id = #{bind('userId')}
				"/>
			</db>
		</idu>
	</script>
```


