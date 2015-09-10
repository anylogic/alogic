# /core/AclQuery

## 概述

查询访问控制器的统计信息。

服务的路径如下：
```
/core/AclQuery
```

## 输入参数
无

## 输入文档
无

## 输出文档

输出为JSON文档，信息如下：
| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | acls | Object | 访问控制列表对象  |  |
| 1.1 | acls/@module | String | 实现模块 | 访问控制器的模块，不同模块的访问控制统计信息可能不一样 |
| 1.2 | acls/acl[] | Object[] | 访问控制主体列表  | | 
| 1.2.1 | acls/acl[]/@session | String | 访问控制主体id | 一个主题一条记录，以本字段标示 |
| 1.2.2 | acls/acl[]/@currentThread | Long | 当前并发数 | |
| 1.2.3 | acls/acl[]/@timesOneMin | Long | 一分钟之内调用次数 | |
| 1.2.4 | acls/acl[]/@timesTotal | Long | 总调用次数 | |
| 1.2.5 | acls/acl[]/@waitCnt | Long | 等待调度的并发数 | | 

## 异常
* 如果调用成功，返回代码为core.ok;

## 样例

下面是一个样例，
服务地址如下；
```
http://localhost:9000/services/core/AclQuery
```
输出结果：
```
	{
	    "duration": "22", 
	    "host": "0:0:0:0:0:0:0:1:9000", 
	    "reason": "It is successful", 
	    "acls": {
	        "module": "com.logicbus.backend.acm.ACMIPAccessController", 
	        "acl": [
	            {
	                "currentThread": "1", 
	                "session": "0:0:0:0:0:0:0:1:/core/AclQuery", 
	                "timesOneMin": "1", 
	                "waitCnt": "0", 
	                "timesTotal": "1"
	            }, 
	            {
	                "currentThread": "0", 
	                "session": "0:0:0:0:0:0:0:1:/core/ServiceQuery", 
	                "timesOneMin": "1", 
	                "waitCnt": "0", 
	                "timesTotal": "1"
	            }
	        ]
	    }, 
	    "code": "core.ok", 
	    "serial": "1441866858422vYiDkXf"
	}

```

