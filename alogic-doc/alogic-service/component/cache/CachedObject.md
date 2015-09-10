# /component/cache/CachedObject

## 概述

在指定缓存中查找指定的对象.

服务的路径如下：
```
/component/cache/CachedObject
```

## 输入参数
| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | cacheId | String | 待查询的缓存id | |
| 2 | objectId | String | 待查询的对象id | |


## 输入文档
无

## 输出文档

输出为JSON文档，信息如下：

| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | cachedObject | Object | 缓存对象 | 对象的具体内容由业务确定 |

## 异常
* 如果调用成功，返回代码为core.ok;
* 如果无法找到指定的缓存，返回代码为user.data_not_found;
* 如果无法找到指定的对象，返回代码为user.data_not_found;

## 样例

下面是一个样例，
服务地址如下；
```
http://localhost:9000/services/component/cache/CachedObject?cacheId=posts&objectId=1
```
输出结果：
```javascript
	{
	    "duration": "0", 
	    "host": "0:0:0:0:0:0:0:1:9000", 
	    "reason": "It is successful", 
	    "cachedObject": {
	        "id": "1", 
	        "name": "软件工程师", 
	        "note": "就是一个岗位"
	    }, 
	    "code": "core.ok", 
	    "serial": "1441872930881l17Akhu"
	}
```

