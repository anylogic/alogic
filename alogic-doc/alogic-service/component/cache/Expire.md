# /component/cache/Expire

## 概述

清除指定缓存中的对象，如果指定了对象id，则只清除对象。

服务的路径如下：
```
/component/cache/Expire
```

## 输入参数
| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | cacheId | String | 待操作的缓存id | |
| 2 | objectId | String | 待操作的对象id | 可选，如果不指定，则清除整个缓存 |

## 输入文档
无

## 输出文档

输出为JSON文档，同[/component/cache/Query](Query.md).

## 异常
* 如果调用成功，返回代码为core.ok;
* 如果无法找到指定的缓存，返回代码为user.data_not_found;

## 样例

下面是一个样例，
服务地址如下；
```
http://localhost:9000/services/component/cache/Expire?cacheId=posts
```
输出结果：
```
	{
	    "duration": "24", 
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
	        "objectCount": 0, 
	        "requestTimes": 11, 
	        "note": ""
	    }, 
	    "reason": "It is successful", 
	    "code": "core.ok", 
	    "serial": "1441875403420JkEgpbV"
	}
```

