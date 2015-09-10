# /core/log/Resume

## 概述

恢复日志处理，并返回处理报告。

服务的路径如下：
```
/core/log/Resume
```

## 输入参数
无

## 输入文档
无

## 输出文档

输出为JSON文档，和[/core/log/Report](Report.md)一致.

## 异常
* 如果调用成功，返回代码为core.ok;

## 样例

下面是一个样例，
服务地址如下；
```
http://localhost:9000/services/core/log/Resume
```
输出结果：
```
	{
	    "duration": "0", 
	    "host": "0:0:0:0:0:0:0:1:9000", 
	    "reason": "It is successful", 
	    "logger": {
	        "total": 2, 
	        "maxQueueLength": "1000", 
	        "module": "com.logicbus.backend.bizlog.BizLogger$Hub", 
	        "interval": 1000, 
	        "isRunning": true, 
	        "item": [
	            {
	                "amount": 1, 
	                "dim": "0:0:0:0:0:0:0:1%/core/ServiceResume%core.ok"
	            }, 
	            {
	                "amount": 1, 
	                "dim": "0:0:0:0:0:0:0:1%/core/ServicePause%core.ok"
	            }
	        ], 
	        "logger": [
	            {
	                "total": 2, 
	                "module": "com.logicbus.backend.bizlog.MetricsBizLogger", 
	                "isRunning": true, 
	                "item": [
	                    {
	                        "amount": 1, 
	                        "dim": "0:0:0:0:0:0:0:1%/core/ServiceResume%core.ok"
	                    }, 
	                    {
	                        "amount": 1, 
	                        "dim": "0:0:0:0:0:0:0:1%/core/ServicePause%core.ok"
	                    }
	                ], 
	                "async": false
	            }
	        ], 
	        "currentQueueLength": "0", 
	        "async": true
	    }, 
	    "code": "core.ok", 
	    "serial": "1441867877800O7Cc31V"
	}


```

