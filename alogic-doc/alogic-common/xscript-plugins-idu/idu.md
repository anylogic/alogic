idu
===
idu用于创建一个namespace，所有在idu中注册了的插件，不需要再通过using引入。

可以在idu下直接使用的插件包括：
- 数据库操作
    - [创建数据库连接db](db.md)
    - [指定id是否可用idExist](idExist.md)
    - [查询列表list](list.md)
    - [查询个体对象query](query.md)
    - [新增语句new](new.md)
    - [删除语句delete](delete.md)
    - [更新语句update](update.md)
    - [提交事务commit](commit.md)
    - [回滚事务rollback](rollback.md)
- 缓存操作
    - [创建缓存连接](cache.md)
    - [获取缓存对象(load)](load.md)
    - [过期指定缓存对象(expire)](expire.md)

### 实现模块

com.alogic.together.idu.IDU

### 配置参数

无

### 案例

通过下列方法使用idu.

```xml
	<script>
		<using xmlTag="idu" module="com.alogic.together.idu.IDU"/>
		<idu>
			<!--下面使用idu注册插件进行操作-->
		</idu>
	</script>
```

