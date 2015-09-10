# /component/dbcp/Source

## 概述

查询dbcp的数据源配置情况.

服务的路径如下：
```
/component/dbcp/Source
```

> alogic-dbcp是一个配置环境框架（com.anysoft.context）的实例，该框架提供了一个多配置环境来配置对象。
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
http://localhost:9000/services/component/dbcp/Source
```
输出结果：
```javascript
{
    "duration": "21", 
    "host": "0:0:0:0:0:0:0:1:9000", 
    "source": {
        "module": "com.logicbus.dbcp.context.DbcpSource", 
        "ctxName": "ds", 
        "ds": [
            {
                "objName": "dbcp", 
                "module": "com.logicbus.dbcp.impl.InnerContext", 
                "dbcp": [
                    {
                        "maxActive": 30, 
                        "id": "itportal", 
                        "maxIdle": 5, 
                        "username": "itportal", 
                        "callbackId": "", 
                        "callback": "", 
                        "maxWait": 10000, 
                        "runtime": {
                            "pool": {
                                "maxActive": 30, 
                                "maxIdle": 5, 
                                "creating": 0, 
                                "idle": 1, 
                                "wait": 0, 
                                "working": 0
                            }, 
                            "stat": {
                                "total": {
                                    "min": "429", 
                                    "max": "659", 
                                    "error": "0", 
                                    "times": "2", 
                                    "avg": "544.00"
                                }, 
                                "module": "com.logicbus.dbcp.util.ConnectionPoolStat", 
                                "start": "20150910161259", 
                                "current": {
                                    "min": "429", 
                                    "max": "659", 
                                    "error": "0", 
                                    "times": "2", 
                                    "avg": "544.00"
                                }, 
                                "lastVistiedTime": "20150910161354", 
                                "cycleStart": "20150910161259"
                            }
                        }, 
                        "coder": "DES3", 
                        "driver": "org.mariadb.jdbc.Driver", 
                        "timeout": 3600, 
                        "url": "jdbc:mariadb://10.142.90.57:8088/itportal"
                    }
                ], 
                "dftClass": "com.logicbus.dbcp.impl.XMLConfigurableImpl", 
                "objCnt": "1"
            }
        ]
    }, 
    "reason": "It is successful", 
    "code": "core.ok", 
    "serial": "14418761617209KIEPXS"
}

```

