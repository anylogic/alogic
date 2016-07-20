rollback
========

rollback用于主动回滚数据库事务。

commit必须在某个db语句之内，参见[db](db.md)。

rollback仅在db的autoCommit设置为false时才可以使用。


### 实现模块

com.alogic.together.idu.Rollback

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | dbconn | 上下文对象的id，缺省为dbconn |

### 案例

参见[commit](commit.md)