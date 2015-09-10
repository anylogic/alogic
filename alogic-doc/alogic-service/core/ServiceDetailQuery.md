# /core/ServiceDetailQuery

## 概述

查询指定ID的服务的信息。

服务的路径如下：
```
/core/ServiceDetailQuery
```

本服务可以简单方式访问，例如：
```
	http://<ip>:<port>/services/服务路径?detail
```

## 输入参数
| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | service | String | service ID  | 需要查询的服务的ID |

## 输入文档
无

## 输出文档

输出为JSON文档，信息如下：
| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | service | Object | 服务对象  | 服务描述的JSON对象，见样例 |

## 异常
* 如果调用成功，返回代码为core.ok;

## 样例

下面是一个样例，
服务地址如下；
```
http://localhost:9000/services/core/ServiceDetailQuery?service=/core/ServiceQuery
```
输出结果：
```
	{
	    "duration": "36", 
	    "host": "0:0:0:0:0:0:0:1:9000", 
	    "reason": "It is successful", 
	    "service": {
	        "id": "ServiceQuery", 
	        "module": "com.logicbus.service.ServiceQuery", 
	        "visible": "system", 
	        "name": "ServiceQuery", 
	        "path": "/core/ServiceQuery", 
	        "runtime": {
	            "pool": {
	                "maxActive": 10, 
	                "maxIdle": 1, 
	                "creating": 0, 
	                "idle": 1, 
	                "wait": 0, 
	                "working": 0
	            }, 
	            "status": "running", 
	            "stat": {
	                "total": {
	                    "min": "368", 
	                    "max": "368", 
	                    "error": "0", 
	                    "times": "1", 
	                    "avg": "368.00"
	                }, 
	                "module": "com.logicbus.backend.ServantStat", 
	                "start": "20150910110711", 
	                "current": {
	                    "min": "368", 
	                    "max": "368", 
	                    "error": "0", 
	                    "times": "1", 
	                    "avg": "368.00"
	                }, 
	                "lastVistiedTime": "20150910110712", 
	                "cycleStart": "20150910110711"
	            }
	        }, 
	        "properties": [
	            {
	                "id": "jsonDefault", 
	                "value": "true"
	            }, 
	            {
	                "id": "servant.maxActive", 
	                "value": "10"
	            }, 
	            {
	                "id": "servant.maxIdle", 
	                "value": "1"
	            }
	        ], 
	        "type": "service", 
	        "log": "none", 
	        "note": "查询系统中所部署的所有服务"
	    }, 
	    "code": "core.ok", 
	    "serial": "1441854582128RCCx8s2"
	}
```

