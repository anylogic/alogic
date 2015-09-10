# /component/cache/List

## 概述

查询当前活跃缓存列表。

服务的路径如下：
```
/component/cache/List
```

## 输入参数
无

## 输入文档
无

## 输出文档

输出为JSON文档，信息如下：

| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | cache | Object [] | 缓存列表 | |

具体的缓存对象定义见[/component/cache/Query](Query.md).

## 异常
* 如果调用成功，返回代码为core.ok;

## 样例

下面是一个样例，
服务地址如下；
```
http://localhost:9000/services/component/cache/List
```
输出结果：
```javascript
	{
	    "duration": "1", 
	    "host": "0:0:0:0:0:0:0:1:9000", 
	    "cache": [
	        {
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
	        {
	            "id": "apps", 
	            "module": "com.alogic.cache.local.HashCacheStore", 
	            "hitTimes": 0, 
	            "name": "", 
	            "hitRate": ".0000", 
	            "provider": {
	                "module": "com.logicbus.provider.SingleSQL", 
	                "stat": {
	                    "total": {
	                        "min": "767", 
	                        "max": "767", 
	                        "error": "1", 
	                        "times": "1", 
	                        "avg": "767.00"
	                    }, 
	                    "module": "com.anysoft.util.SimpleCounter", 
	                    "start": "20150910160820", 
	                    "current": {
	                        "min": "767", 
	                        "max": "767", 
	                        "error": "1", 
	                        "times": "1", 
	                        "avg": "767.00"
	                    }, 
	                    "lastVistiedTime": "20150910161300", 
	                    "cycleStart": "20150910161000"
	                }
	            }, 
	            "policy": { }, 
	            "objectCount": 0, 
	            "requestTimes": 1, 
	            "note": ""
	        }
	    ], 
	    "reason": "It is successful", 
	    "code": "core.ok", 
	    "serial": "14418736830671VteGFS"
	}


```

