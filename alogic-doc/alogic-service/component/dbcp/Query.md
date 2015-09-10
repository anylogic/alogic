# /component/dbcp/Query

## 概述

查询指定id的dbcp信息。

服务的路径如下：
```
/component/dbcp/Query
```

## 输入参数
| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | id | String | 待查询的id | |

## 输入文档
无

## 输出文档

输出为JSON文档，信息如下：
| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | pool | Object | dbcp对象 | |
| 1.1 | pool/@id | String | id | |
| 1.2 | pool/@maxActive | Long | 最大的连接数 | | 
| 1.3 | pool/@maxIdle | Long | 最大的空闲连接数 | |
| 1.4 | pool/@username | String | 数据库连接的用户名 | |
| 1.5 | pool/@maxWait | Long | 最大的等待时间(毫秒) | |
| 1.6 | pool/@coder | String | 密码加密算法 | |
| 1.7 | pool/@driver | String | JDBC驱动名 | |
| 1.8 | pool/@url | String | 数据库连接地址 | |
| 1.9 | pool/runtime | Object | 实时运行信息 | |
| 1.9.1 | pool/runtime/pool | Object | 连接池信息 | |
| 1.9.2 | pool/runtime/stat | Object | 统计信息 | |

统计对象的信息如下：
| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | @module | String | 实现模块 | |
| 2 | @start | String | 启动时间 | |
| 3 | @lastVistiedTime | String | 最近访问时间 | |
| 4 | @cycleStart | String | 当前周期开始时间 | |
| 5 | total | Object | 总体统计信息 | 从启动至今 | 
| 5.1 | total/@min | Long | 最小耗时(毫秒) | |
| 5.2 | total/@max | Long | 最大耗时(毫秒) | |
| 5.3 | total/@error | Long | 错误次数 | |
| 5.4 | total/@times | Long | 调用次数 | |
| 5.5 | total/@avg | double | 平均耗时(毫秒) | | 
| 6 | current | Object | 当前周期统计信息 | 从当前周期开始时间至今 |
| 6.1 | current/@min | Long | 最小耗时(毫秒) | |
| 6.2 | current/@max | Long | 最大耗时(毫秒) | |
| 6.3 | current/@error | Long | 错误次数 | |
| 6.4 | current/@times | Long | 调用次数 | |
| 6.5 | current/@avg | double | 平均耗时(毫秒) | | 

连接池信息如下：
| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | @maxActive | Long | 最大的实例个数 | |
| 2 | @maxIdle | Long | 最大的空闲个数 | |
| 3 | @creating | Long | 正在创建对象的线程数 | |
| 4 | @idle | Long | 池中空闲对象个数 | |
| 5 | @wait | Long | 等待分配连接的线程数 | |
| 6 | @working | Long | 正在工作的线程数

## 异常
* 如果调用成功，返回代码为core.ok;

## 样例

下面是一个样例，
服务地址如下；
```
http://localhost:9000/services/component/dbcp/Query?id=itportal
```
输出结果：
```
	{
	    "duration": "17", 
	    "host": "0:0:0:0:0:0:0:1:9000", 
	    "reason": "It is successful", 
	    "dbcp": {
	        "maxActive": 30, 
	        "id": "itportal", 
	        "maxIdle": 5, 
	        "username": "itportal", 
	        "callbackId": "", 
	        "callback": "", 
	        "maxWait": 10000, 
	        "runtime": {
	            "pool": {
	                "maxActive": 30, 
	                "maxIdle": 5, 
	                "creating": 0, 
	                "idle": 0, 
	                "wait": 0, 
	                "working": 0
	            }, 
	            "stat": {
	                "total": {
	                    "min": "100000", 
	                    "max": "0", 
	                    "error": "0", 
	                    "times": "0", 
	                    "avg": ".00"
	                }, 
	                "module": "com.logicbus.dbcp.util.ConnectionPoolStat", 
	                "start": "20150910171821", 
	                "current": {
	                    "min": "100000", 
	                    "max": "0", 
	                    "error": "0", 
	                    "times": "0", 
	                    "avg": ".00"
	                }, 
	                "lastVistiedTime": "20150910171821", 
	                "cycleStart": "20150910171821"
	            }
	        }, 
	        "coder": "DES3", 
	        "driver": "org.mariadb.jdbc.Driver", 
	        "timeout": 3600, 
	        "url": "jdbc:mariadb://10.142.90.57:8088/itportal"
	    }, 
	    "code": "core.ok", 
	    "serial": "14418767014318OQ6nFr"
	}

```


