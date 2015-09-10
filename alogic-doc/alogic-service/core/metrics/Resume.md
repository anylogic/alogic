# /core/metrics/Resume

## 概述

恢复指标处理，并报告指标的处理情况。

服务的路径如下：
```
/core/metrics/Resume
```

## 输入参数
无

## 输入文档
无

## 输出文档

输出为JSON文档，和[/core/metrics/Report](Report.md)一致.

## 异常
* 如果调用成功，返回代码为core.ok;

## 样例

下面是一个样例，
服务地址如下；
```
http://localhost:9000/services/core/metrics/Resume
```
输出结果：
```
	{
	    "duration": "20", 
	    "host": "10.19.156.31:9000", 
	    "reason": "It is successful", 
	    "code": "core.ok", 
	    "serial": "1441869592606fr9jEne", 
	    "handler": {
	        "total": 4, 
	        "module": "com.anysoft.metrics.handler.Default", 
	        "isRunning": true, 
	        "item": [
	            {
	                "amount": 2, 
	                "dim": "svc.thpt:/core/ServiceResume"
	            }, 
	            {
	                "amount": 2, 
	                "dim": "svc.thpt:/core/ServicePause"
	            }
	        ], 
	        "async": false
	    }
	}
```

