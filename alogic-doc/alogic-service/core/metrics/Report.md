# /core/metrics/Report

## 概述

报告指标的处理情况。

> alogic的指标处理是一个基于有向树的流处理框架。本服务将查询出该有向树所有节点的处理情况，所以服务返回将会根据有向树配置的不同而不同。

服务的路径如下：
```
/core/metrics/Report
```

## 输入参数
无

## 输入文档
无

## 输出文档

输出为JSON文档，信息如下：

| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | handler | Object | handler对象 | |
| 1.1 | handler/@module | String | 实现模块 | 实现模块不同，本对象的输出属性可能不同 |
| 1.2 | handler/@total | Long | 数据条数 | |
| 1.3 | handler/@maxQueueLength | Long | 最大队列长度 | 仅异步模式有效 |
| 1.4 | handler/@interval | Long | 处理间隔(毫秒) | 仅异步模式有效 |
| 1.5 | handler/@isRunning | Boolean | 是否在运行 | |
| 1.6 | handler/@currentQueueLength | Long | 当前队列长度 | 仅异步模式有效 |
| 1.7 | handler/@async | Boolean | 是否开启异步模式 |
| 1.8 | handler/item[] | Object[] | 统计项列表 |
| 1.8.1 | handler/item[]/@dim | 统计维度 | |
| 1.8.2 | handler/item[]/@amount | 数据条数 | |
| 1.9 | handler/handler[] | Object[] | 有向树的子节点 | 子节点同样是handler对象 |

## 异常
* 如果调用成功，返回代码为core.ok;

## 样例

下面是一个样例，
服务地址如下；
```
http://localhost:9000/services/core/metrics/Report
```
输出结果：
```javascript
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

