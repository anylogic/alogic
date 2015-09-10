# /core/log/Report

## 概述

报告日志处理情况。

> alogic的日志处理是一个基于有向树的流处理框架。本服务将查询出该有向树所有节点的处理情况，所以服务返回将会根据有向树配置的不同而不同。

服务的路径如下：
```
/core/log/Report
```

## 输入参数
无

## 输入文档
无

## 输出文档

输出为JSON文档，信息如下：

| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | logger | Object | Logger对象 | |
| 1.1 | logger/@module | String | 实现模块 | 实现模块不同，本对象的输出属性可能不同 |
| 1.2 | logger/@total | Long | 数据条数 | |
| 1.3 | logger/@maxQueueLength | Long | 最大队列长度 | 仅异步模式有效 |
| 1.4 | logger/@interval | Long | 处理间隔(毫秒) | 仅异步模式有效 |
| 1.5 | logger/@isRunning | Boolean | 是否在运行 | |
| 1.6 | logger/@currentQueueLength | Long | 仅异步模式有效 | |
| 1.7 | logger/@async | Boolean | 是否开启异步模式 |
| 1.8 | logger/item[] | Object[] | 统计项列表 |
| 1.8.1 | logger/item[]/@dim | 统计维度 | 服务ID%错误代码 |
| 1.8.2 | logger/item[]/@amount | 数据条数 | |
| 1.9 | logger/logger[] | Object[] | 有向树的子节点 | 子节点同样是logger对象 |

## 异常
* 如果调用成功，返回代码为core.ok;

## 样例

下面是一个样例，
服务地址如下；
```
http://localhost:9000/services/core/log/Report
```
输出结果：
```javascript
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

