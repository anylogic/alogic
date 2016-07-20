idExist
=======

idExist用于检查指定的id是否可用。

> idExist实际上是检查数据库中是否存在指定的记录。如果记录存在，则抛出core.id_used异常。

本插件中所使用的SQL支持动态SQL语句，参考[sqlprocessor](../sqlprocessor.md)。

idExist必须在某个db语句之内，参见[db](db.md)。

### 实现模块

com.alogic.together.idu.IdExist

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | dbconn | 上下文对象的id，缺省为dbconn |
| 2 | sql.IdExist | SQL语句，缺省为selet 1 from dual |

### 案例

下面是一个简单的检测某个appId是否可用的案例.

```xml
	<script>
		<using xmlTag="idu" module="com.alogic.together.idu.IDU"/>
		<!--前端必须输入参数appId-->
		<check id="appId"/>
		<idu>
			<db>
				<!--指定的appId在app表中没有使用过-->
				<idExist sql.IdExist="
					select count(*) from app where app_id = #{bind('appId')}
				"/>
			</db>
		</idu>
	</script>
```


