# /component/cache/Query

## 概述

查询指定id的缓存信息。

服务的路径如下：
```
/component/cache/Query
```

## 输入参数
| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | cacheId | String | 待查询的缓存id | |


## 输入文档
无

## 输出文档

输出为JSON文档，信息如下：

| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | cache | Object | 缓存对象 | |
| 1.1 | cache/@id | String | 缓存的唯一id | | 
| 1.2 | cache/@module | String | 缓存实现模块 | | 
| 1.3 | cache/@name | String | 名称 | |
| 1.4 | cache/@note | String | 说明 | | 
| 1.5 | cache/@hitRate | Double | 命中率 | 部分实现提供 |
| 1.6 | cache/@hitTimes | Long | 命中次数 | 部分实现提供  |
| 1.7 | cache/@objectCount | Long | 缓存对象个数 | 部分实现提供 |
| 1.8 | cache/@requestTimes | Long | 请求次数 | 部分实现提供 | 
| 1.9 | cache/provider | Object | 提供者对象 | |
| 1.9.1 | cache/provider/@module | String | 提供者实现模块 |
| 1.9.2 | cache/provider/stat | Object | 提供者的统计对象 | 

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

## 异常
* 如果调用成功，返回代码为core.ok;
* 如果无法找到指定的缓存，返回代码为user.data_not_found;

## 样例

下面是一个样例，
服务地址如下；
```
http://localhost:9000/services/component/cache/Query?cacheId=posts
```
输出结果：
```javascript
	{
	    "duration": "33", 
	    "host": "0:0:0:0:0:0:0:1:9000", 
	    "cache": {
	        "id": "posts", 
	        "module": "com.alogic.cache.local.HashCacheStore", 
	        "hitTimes": 10, 
	        "name": "", 
	        "hitRate": ".9091", 
	        "provider": {
	            "module": "com.logicbus.provider.SingleSQL", 
	            "stat": {
	                "total": {
	                    "min": "514", 
	                    "max": "514", 
	                    "error": "0", 
	                    "times": "1", 
	                    "avg": "514.00"
	                }, 
	                "module": "com.anysoft.util.SimpleCounter", 
	                "start": "20150910160820", 
	                "current": {
	                    "min": "514", 
	                    "max": "514", 
	                    "error": "0", 
	                    "times": "1", 
	                    "avg": "514.00"
	                }, 
	                "lastVistiedTime": "20150910161354", 
	                "cycleStart": "20150910161000"
	            }
	        }, 
	        "policy": { }, 
	        "objectCount": 1, 
	        "requestTimes": 11, 
	        "note": ""
	    }, 
	    "reason": "It is successful", 
	    "code": "core.ok", 
	    "serial": "1441873957640FgQdTcv"
	}

```

