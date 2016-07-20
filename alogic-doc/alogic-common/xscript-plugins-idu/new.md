new
===
new用于执行insert语句。

本插件中所使用的SQL支持动态SQL语句，参考[sqlprocessor](../sqlprocessor.md)。

new必须在某个db语句之内，参见[db](db.md)。

### 实现模块

com.alogic.together.idu.New

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | dbconn | 上下文对象的id，缺省为dbconn |
| 2 | sql.Insert | 插入SQL语句 |

### 案例

下面案例从数据库中新增用户.

```xml
	<script>
		<using xmlTag="idu" module="com.alogic.together.idu.IDU"/>
		<check id="userId"/>
		<idu>
			<db>
				<new sql.Insert="
					insert into user
					(
						user_id
						#{not_nvl(name,',name')}
						#{not_nvl(email,',email')}
						#{not_nvl(mobile,',mobile')}
					)
					values(
						#{bind('userId')}
						#{not_nvl(name,',' + bind('name'))}
						#{not_nvl(email,',' + bind('email'))}
						#{not_nvl(mobile,',' + bind('mobile'))}
					)
				"/>
			</db>
		</idu>
	</script>
```


