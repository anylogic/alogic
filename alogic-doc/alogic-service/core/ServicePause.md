# /core/ServicePause

## 概述

暂停服务的处理，操作之后，将不再接受服务请求。

暂停之后的服务可以通过[恢复服务](ServiceResume.md)来恢复。

服务的路径如下：
```
/core/ServicePause
```

本服务可以简单方式访问，例如：
```
	http://<ip>:<port>/services/服务路径?pause
```

## 输入参数
| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | service | String | service ID  | 待操作服务的ID |

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
http://localhost:9000/services/core/ServiceList?pause
```
输出结果：
```
	{
	    "duration": "45", 
	    "host": "0:0:0:0:0:0:0:1:9000", 
	    "reason": "It is successful", 
	    "service": {
	        "id": "ServiceList", 
	        "module": "com.logicbus.service.ServiceList", 
	        "visible": "system", 
	        "name": "ServiceList", 
	        "path": "/core/ServiceList", 
	        "runtime": {
	            "pool": {
	                "maxActive": 10, 
	                "maxIdle": 1, 
	                "creating": 0, 
	                "idle": 0, 
	                "wait": 0, 
	                "working": 0
	            }, 
	            "status": "pause", 
	            "stat": {
	                "total": {
	                    "min": "144", 
	                    "max": "178", 
	                    "error": "0", 
	                    "times": "5", 
	                    "avg": "162.20"
	                }, 
	                "module": "com.logicbus.backend.ServantStat", 
	                "start": "20150910125407", 
	                "current": {
	                    "min": "144", 
	                    "max": "178", 
	                    "error": "0", 
	                    "times": "4", 
	                    "avg": "162.25"
	                }, 
	                "lastVistiedTime": "20150910125936", 
	                "cycleStart": "20150910125500"
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
	        "note": "查询系统中所部署的所有服务列表(以列表形式输出)"
	    }, 
	    "code": "core.ok", 
	    "serial": "1441861582398uYd7H8k"
	}

```

