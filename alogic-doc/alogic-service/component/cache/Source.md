# /component/cache/Source

## 概述

查询缓存的数据源配置情况.

服务的路径如下：
```
/component/cache/Source
```

> alogic-cache是一个配置环境框架（com.anysoft.context）的实例，该框架提供了一个多配置环境来配置对象。
> 因此数据源配置情况将根据配置的不同而不同。

## 输入参数
无

## 输入文档
无

## 输出文档

输出为JSON文档，信息如下：

| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | source | Object | 配置源对象 | 对象内容根据配置情况不同而不同，具体见案例 |

## 异常
* 如果调用成功，返回代码为core.ok;

## 样例

下面是一个样例，
服务地址如下；
```
http://localhost:9000/services/component/cache/Source
```
输出结果：
```javascript
	{
	    "duration": "1", 
	    "host": "0:0:0:0:0:0:0:1:9000", 
	    "source": {
	        "module": "com.alogic.cache.context.CacheSource", 
	        "ctxName": "context", 
	        "context": [
	            {
	                "objName": "cache", 
	                "module": "com.alogic.cache.context.InnerContext", 
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
	                    }, 
	                    {
	                        "id": "approles", 
	                        "module": "com.alogic.cache.local.HashCacheStore", 
	                        "hitTimes": 0, 
	                        "name": "", 
	                        "hitRate": ".0000", 
	                        "provider": {
	                            "module": "com.logicbus.provider.SingleSQL", 
	                            "stat": {
	                                "total": {
	                                    "min": "100000", 
	                                    "max": "0", 
	                                    "error": "0", 
	                                    "times": "0", 
	                                    "avg": ".00"
	                                }, 
	                                "module": "com.anysoft.util.SimpleCounter", 
	                                "start": "20150910160820", 
	                                "current": {
	                                    "min": "100000", 
	                                    "max": "0", 
	                                    "error": "0", 
	                                    "times": "0", 
	                                    "avg": ".00"
	                                }, 
	                                "lastVistiedTime": "20150910160820", 
	                                "cycleStart": "20150910160820"
	                            }
	                        }, 
	                        "policy": { }, 
	                        "objectCount": 0, 
	                        "requestTimes": 0, 
	                        "note": ""
	                    }
	                ], 
	                "dftClass": "com.alogic.cache.local.SlottedCacheStore", 
	                "objCnt": "3"
	            }
	        ]
	    }, 
	    "reason": "It is successful", 
	    "code": "core.ok", 
	    "serial": "1441873341811GttL4zr"
	}

```

