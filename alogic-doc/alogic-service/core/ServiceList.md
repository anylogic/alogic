# /core/ServiceList

## 概述

查询服务列表,以列表的形式返回。

服务的路径如下：
```
/core/ServiceList
```

## 输入参数
无

## 输入文档
无

## 输出文档

输出为JSON文档，信息如下：

| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | service | Object[] | 服务列表  | 服务列表描述的JSON对象，见样例 |

## 异常
* 如果调用成功，返回代码为core.ok;

## 样例

下面是一个样例，
服务地址如下；
```
http://localhost:9000/services/core/ServiceList
```
输出结果：
```javascript
	{
	    "duration": "178", 
	    "host": "0:0:0:0:0:0:0:1:9000", 
	    "reason": "It is successful", 
	    "service": [
	        {
	            "id": "ServiceList", 
	            "module": "com.logicbus.service.ServiceList", 
	            "visible": "system", 
	            "name": "ServiceList", 
	            "path": "/core/ServiceList", 
	            "log": "none", 
	            "note": "查询系统中所部署的所有服务列表(以列表形式输出)"
	        }, 
	        {
	            "id": "ServiceDetailQuery", 
	            "module": "com.logicbus.service.ServiceDetailQuery", 
	            "visible": "system", 
	            "name": "ServiceDetailQuery", 
	            "path": "/core/ServiceDetailQuery", 
	            "log": "none", 
	            "note": "查询指定的服务信息"
	        }, 
	        {
	            "id": "ServiceResume", 
	            "module": "com.logicbus.service.ServiceResume", 
	            "visible": "system", 
	            "name": "ServiceResume", 
	            "path": "/core/ServiceResume", 
	            "log": "brief", 
	            "note": "恢复服务"
	        }, 
	        {
	            "id": "ServiceReload", 
	            "module": "com.logicbus.service.ServiceReload", 
	            "visible": "system", 
	            "name": "ServiceReload", 
	            "path": "/core/ServiceReload", 
	            "log": "brief", 
	            "note": "重新装入服务缓冲池"
	        }, 
	        {
	            "id": "AclQuery", 
	            "module": "com.logicbus.service.AclQuery", 
	            "visible": "system", 
	            "name": "AclQuery", 
	            "path": "/core/AclQuery", 
	            "log": "none", 
	            "note": "查询当前的访问控制列表"
	        }, 
	        {
	            "id": "ServiceQuery", 
	            "module": "com.logicbus.service.ServiceQuery", 
	            "visible": "system", 
	            "name": "ServiceQuery", 
	            "path": "/core/ServiceQuery", 
	            "log": "none", 
	            "note": "查询系统中所部署的所有服务列表(以服务目录形式输出)"
	        }, 
	        {
	            "id": "ServicePause", 
	            "module": "com.logicbus.service.ServicePause", 
	            "visible": "system", 
	            "name": "ServicePause", 
	            "path": "/core/ServicePause", 
	            "log": "brief", 
	            "note": "暂停服务"
	        }, 
	        {
	            "id": "Env", 
	            "module": "com.logicbus.service.EnvQuery", 
	            "visible": "public", 
	            "name": "Env", 
	            "path": "/core/util/Env", 
	            "log": "none", 
	            "note": "查询JVM环境变量"
	        }, 
	        {
	            "id": "Settings", 
	            "module": "com.logicbus.service.SettingsQuery", 
	            "visible": "system", 
	            "name": "Settings", 
	            "path": "/core/util/Settings", 
	            "log": "none", 
	            "note": "查询节点的环境变量"
	        }, 
	        {
	            "id": "GC", 
	            "module": "com.logicbus.service.GC", 
	            "visible": "system", 
	            "name": "GC", 
	            "path": "/core/util/GC", 
	            "log": "brief", 
	            "note": "主动触发虚拟机回收内存"
	        }, 
	        {
	            "id": "Proxy", 
	            "module": "com.logicbus.service.Proxy", 
	            "visible": "system", 
	            "name": "Proxy", 
	            "path": "/core/util/Proxy", 
	            "log": "brief", 
	            "note": "代理服务"
	        }, 
	        {
	            "id": "Report", 
	            "module": "com.logicbus.service.BizLogReport", 
	            "visible": "system", 
	            "name": "Report", 
	            "path": "/core/log/Report", 
	            "log": "none", 
	            "note": "业务日志报告"
	        }, 
	        {
	            "id": "Pause", 
	            "module": "com.logicbus.service.BizLoggerPause", 
	            "visible": "system", 
	            "name": "Pause", 
	            "path": "/core/log/Pause", 
	            "log": "none", 
	            "note": "暂停BizLogger的处理"
	        }, 
	        {
	            "id": "Resume", 
	            "module": "com.logicbus.service.BizLoggerResume", 
	            "visible": "system", 
	            "name": "Resume", 
	            "path": "/core/log/Resume", 
	            "log": "none", 
	            "note": "恢复BizLogger的处理"
	        }, 
	        {
	            "id": "Report", 
	            "module": "com.logicbus.service.MetricsReport", 
	            "visible": "system", 
	            "name": "Report", 
	            "path": "/core/metrics/Report", 
	            "log": "none", 
	            "note": "指标处理报告"
	        }, 
	        {
	            "id": "Pause", 
	            "module": "com.logicbus.service.MetricsPause", 
	            "visible": "system", 
	            "name": "Pause", 
	            "path": "/core/metrics/Pause", 
	            "log": "brief", 
	            "note": "暂停MetricsHandler的处理"
	        }, 
	        {
	            "id": "Resume", 
	            "module": "com.logicbus.service.MetricsResume", 
	            "visible": "system", 
	            "name": "Resume", 
	            "path": "/core/metrics/Resume", 
	            "log": "brief", 
	            "note": "恢复MetricsHandler的处理"
	        }, 
	        {
	            "id": "Query", 
	            "module": "com.alogic.cache.service.CacheQuery", 
	            "visible": "system", 
	            "name": "Query", 
	            "path": "/component/cache/Query", 
	            "log": "none", 
	            "note": "查询指定cache信息"
	        }, 
	        {
	            "id": "List", 
	            "module": "com.alogic.cache.service.CacheList", 
	            "visible": "system", 
	            "name": "List", 
	            "path": "/component/cache/List", 
	            "log": "none", 
	            "note": "列出当前存在的cache列表"
	        }, 
	        {
	            "id": "Source", 
	            "module": "com.alogic.cache.service.CacheSourceReport", 
	            "visible": "system", 
	            "name": "Source", 
	            "path": "/component/cache/Source", 
	            "log": "none", 
	            "note": "cache的Source报告"
	        }, 
	        {
	            "id": "CachedObject", 
	            "module": "com.alogic.cache.service.CacheObjectQuery", 
	            "visible": "system", 
	            "name": "CachedObject", 
	            "path": "/component/cache/CachedObject", 
	            "log": "none", 
	            "note": "查询指定cache信息的指定对象"
	        }, 
	        {
	            "id": "Expire", 
	            "module": "com.alogic.cache.service.CacheExpire", 
	            "visible": "system", 
	            "name": "Expire", 
	            "path": "/component/cache/Expire", 
	            "log": "none", 
	            "note": "过期指定的cache或者cache中指定的数据"
	        }, 
	        {
	            "id": "Query", 
	            "module": "com.logicbus.dbcp.service.DbcpList", 
	            "visible": "system", 
	            "name": "Query", 
	            "path": "/component/dbcp/Query", 
	            "log": "none", 
	            "note": "查询指定ID的数据库连接池信息"
	        }, 
	        {
	            "id": "List", 
	            "module": "com.logicbus.dbcp.service.DbcpList", 
	            "visible": "system", 
	            "name": "List", 
	            "path": "/component/dbcp/List", 
	            "log": "none", 
	            "note": "查询活动的数据库连接池列表"
	        }, 
	        {
	            "id": "Source", 
	            "module": "com.logicbus.dbcp.service.DbcpSourceReport", 
	            "visible": "system", 
	            "name": "Source", 
	            "path": "/component/dbcp/Source", 
	            "log": "none", 
	            "note": "查询数据源信息"
	        }, 
	        {
	            "id": "Query", 
	            "module": "com.logicbus.remote.service.CallQuery", 
	            "visible": "system", 
	            "name": "Query", 
	            "path": "/component/remote/Query", 
	            "log": "none", 
	            "note": "指定远程调用信息"
	        }, 
	        {
	            "id": "List", 
	            "module": "com.logicbus.remote.service.CallList", 
	            "visible": "system", 
	            "name": "List", 
	            "path": "/component/remote/List", 
	            "log": "none", 
	            "note": "活跃远程调用的列表"
	        }, 
	        {
	            "id": "Source", 
	            "module": "com.logicbus.remote.service.CallSourceReport", 
	            "visible": "system", 
	            "name": "Source", 
	            "path": "/component/remote/Source", 
	            "log": "none", 
	            "note": "远程调用报告"
	        }, 
	        {
	            "id": "Query", 
	            "module": "com.logicbus.kvalue.service.SchemaQuery", 
	            "visible": "system", 
	            "name": "Query", 
	            "path": "/component/kvalue/Query", 
	            "log": "none", 
	            "note": "查询指定的Schema"
	        }, 
	        {
	            "id": "List", 
	            "module": "com.logicbus.kvalue.service.SchemaList", 
	            "visible": "system", 
	            "name": "List", 
	            "path": "/component/kvalue/List", 
	            "log": "none", 
	            "note": "查询活跃的Schema列表"
	        }, 
	        {
	            "id": "Source", 
	            "module": "com.logicbus.kvalue.service.SchemaSourceReport", 
	            "visible": "system", 
	            "name": "Source", 
	            "path": "/component/kvalue/Source", 
	            "log": "none", 
	            "note": "Schema配置来源报告"
	        }, 
	        {
	            "id": "Query", 
	            "module": "com.alogic.seq.service.SeqQuery", 
	            "visible": "system", 
	            "name": "Query", 
	            "path": "/component/seq/Query", 
	            "log": "none", 
	            "note": "查询指定的seq"
	        }, 
	        {
	            "id": "List", 
	            "module": "com.alogic.seq.service.SeqList", 
	            "visible": "system", 
	            "name": "List", 
	            "path": "/component/seq/List", 
	            "log": "none", 
	            "note": "查询活跃的seq列表"
	        }, 
	        {
	            "id": "Source", 
	            "module": "com.alogic.seq.service.SeqSourceReport", 
	            "visible": "system", 
	            "name": "Source", 
	            "path": "/component/seq/Source", 
	            "log": "none", 
	            "note": "全局序列配置来源报告"
	        }, 
	        {
	            "id": "Download", 
	            "module": "com.alogic.blob.service.Download", 
	            "visible": "protected", 
	            "name": "Download", 
	            "path": "/component/blob/Download", 
	            "log": "brief", 
	            "note": "下载Blob文件"
	        }, 
	        {
	            "id": "Query", 
	            "module": "com.alogic.blob.service.BlobQuery", 
	            "visible": "system", 
	            "name": "Query", 
	            "path": "/component/blob/Query", 
	            "log": "none", 
	            "note": "查询指定的BlobManager"
	        }, 
	        {
	            "id": "List", 
	            "module": "com.alogic.blob.service.BlobList", 
	            "visible": "system", 
	            "name": "List", 
	            "path": "/component/blob/List", 
	            "log": "none", 
	            "note": "查询活跃的BlobManager列表"
	        }, 
	        {
	            "id": "Source", 
	            "module": "com.alogic.blob.service.BlobSourceReport", 
	            "visible": "system", 
	            "name": "Source", 
	            "path": "/component/blob/Source", 
	            "log": "none", 
	            "note": "Blob来源报告"
	        }, 
	        {
	            "id": "Report", 
	            "module": "com.alogic.timer.service.SchedulerReport", 
	            "visible": "protected", 
	            "name": "Report", 
	            "path": "/component/scheduler/Report", 
	            "log": "none", 
	            "note": "查询调度器信息"
	        }, 
	        {
	            "id": "Pause", 
	            "module": "com.alogic.timer.service.SchedulerPause", 
	            "visible": "protected", 
	            "name": "Pause", 
	            "path": "/component/scheduler/Pause", 
	            "log": "none", 
	            "note": "暂停调度器"
	        }, 
	        {
	            "id": "Resume", 
	            "module": "com.alogic.timer.service.SchedulerResume", 
	            "visible": "protected", 
	            "name": "Resume", 
	            "path": "/component/scheduler/Resume", 
	            "log": "none", 
	            "note": "恢复调度器"
	        }, 
	        {
	            "id": "Report", 
	            "module": "com.alogic.doer.service.TaskCenterReport", 
	            "visible": "protected", 
	            "name": "Report", 
	            "path": "/component/scheduler/tc/Report", 
	            "log": "none", 
	            "note": "查询任务中心信息"
	        }, 
	        {
	            "id": "TaskReport", 
	            "module": "com.alogic.doer.service.TaskReportReport", 
	            "visible": "protected", 
	            "name": "TaskReport", 
	            "path": "/component/scheduler/tc/TaskReport", 
	            "log": "none", 
	            "note": "查询指定的任务信息"
	        }, 
	        {
	            "id": "Report", 
	            "module": "com.alogic.timer.service.TimerReport", 
	            "visible": "protected", 
	            "name": "Report", 
	            "path": "/component/scheduler/timer/Report", 
	            "log": "none", 
	            "note": "查询指定的定时器信息"
	        }, 
	        {
	            "id": "Pause", 
	            "module": "com.alogic.timer.service.TimerPause", 
	            "visible": "protected", 
	            "name": "Pause", 
	            "path": "/component/scheduler/timer/Pause", 
	            "log": "none", 
	            "note": "暂停指定的定时器"
	        }, 
	        {
	            "id": "Resume", 
	            "module": "com.alogic.timer.service.TimerResume", 
	            "visible": "protected", 
	            "name": "Resume", 
	            "path": "/component/scheduler/timer/Resume", 
	            "log": "none", 
	            "note": "恢复指定的定时器"
	        }, 
	        {
	            "id": "Simulator", 
	            "module": "com.logicbus.service.Simulator", 
	            "visible": "public", 
	            "name": "Simulator", 
	            "path": "/demo/Simulator", 
	            "log": "detail", 
	            "note": "模拟服务调用过程,耗时按正态进行分布,用于系统框架的并发测试"
	        }, 
	        {
	            "id": "Helloworld", 
	            "module": "demo.service.Helloworld", 
	            "visible": "public", 
	            "name": "Helloworld", 
	            "path": "/demo/Helloworld", 
	            "log": "detail", 
	            "note": "Helloworld"
	        }, 
	        {
	            "id": "UnMark", 
	            "module": "com.ketty.service.UnMark", 
	            "visible": "protected", 
	            "name": "UnMark", 
	            "path": "/alogic/UnMark", 
	            "log": "none", 
	            "note": "向Zookeeper取消标记服务器"
	        }, 
	        {
	            "id": "ARMQuery", 
	            "module": "com.ketty.service.ARMQuery", 
	            "visible": "protected", 
	            "name": "ARMQuery", 
	            "path": "/alogic/ARMQuery", 
	            "log": "none", 
	            "note": "查询ARM的信息"
	        }, 
	        {
	            "id": "ARMReload", 
	            "module": "com.ketty.service.ARMReload", 
	            "visible": "protected", 
	            "name": "ARMReload", 
	            "path": "/alogic/ARMReload", 
	            "log": "brief", 
	            "note": "重新装入ARM信息"
	        }, 
	        {
	            "id": "Mark", 
	            "module": "com.ketty.service.Mark", 
	            "visible": "protected", 
	            "name": "Mark", 
	            "path": "/alogic/Mark", 
	            "log": "brief", 
	            "note": "向Zookeeper标记服务器"
	        }, 
	        {
	            "id": "Proxy", 
	            "module": "com.ketty.service.AppProxy", 
	            "visible": "protected", 
	            "name": "Proxy", 
	            "path": "/alogic/Proxy", 
	            "log": "brief", 
	            "note": "基于App的服务调用代理"
	        }, 
	        {
	            "id": "SystemProperties", 
	            "module": "com.ketty.service.JVMSystemProperties", 
	            "visible": "protected", 
	            "name": "SystemProperties", 
	            "path": "/alogic/jvm/SystemProperties", 
	            "log": "none", 
	            "note": "获取JVM的SystemProperties信息"
	        }, 
	        {
	            "id": "Env", 
	            "module": "com.ketty.service.JVMEnv", 
	            "visible": "protected", 
	            "name": "Env", 
	            "path": "/alogic/jvm/Env", 
	            "log": "none", 
	            "note": "获取JVM的Env信息"
	        }, 
	        {
	            "id": "Runtime", 
	            "module": "com.ketty.service.JVMRuntime", 
	            "visible": "protected", 
	            "name": "Runtime", 
	            "path": "/alogic/jvm/Runtime", 
	            "log": "none", 
	            "note": "获取JVM的Runtime信息"
	        }, 
	        {
	            "id": "KettySettings", 
	            "module": "com.ketty.service.JVMKettySettings", 
	            "visible": "protected", 
	            "name": "KettySettings", 
	            "path": "/alogic/jvm/KettySettings", 
	            "log": "none", 
	            "note": "获取JVM的KettySettings信息"
	        }, 
	        {
	            "id": "FileSystem", 
	            "module": "com.ketty.service.HostFileSystem", 
	            "visible": "protected", 
	            "name": "FileSystem", 
	            "path": "/alogic/host/FileSystem", 
	            "log": "none", 
	            "note": "获取主机的文件系统信息"
	        }, 
	        {
	            "id": "Mem", 
	            "module": "com.ketty.service.HostMem", 
	            "visible": "protected", 
	            "name": "Mem", 
	            "path": "/alogic/host/Mem", 
	            "log": "none", 
	            "note": "获取主机的内存及SWAP的使用信息"
	        }, 
	        {
	            "id": "CPU", 
	            "module": "com.ketty.service.HostCPU", 
	            "visible": "protected", 
	            "name": "CPU", 
	            "path": "/alogic/host/CPU", 
	            "log": "none", 
	            "note": "获取主机的CPU使用信息"
	        }, 
	        {
	            "id": "NetStat", 
	            "module": "com.ketty.service.HostNetStat", 
	            "visible": "protected", 
	            "name": "NetStat", 
	            "path": "/alogic/host/NetStat", 
	            "log": "none", 
	            "note": "获取主机的统计信息"
	        }, 
	        {
	            "id": "Process", 
	            "module": "com.ketty.service.HostProcess", 
	            "visible": "protected", 
	            "name": "Process", 
	            "path": "/alogic/host/Process", 
	            "log": "none", 
	            "note": "查询指定进程的信息"
	        }, 
	        {
	            "id": "NetInfo", 
	            "module": "com.ketty.service.HostNetInfo", 
	            "visible": "protected", 
	            "name": "NetInfo", 
	            "path": "/alogic/host/NetInfo", 
	            "log": "none", 
	            "note": "获取主机的网络信息"
	        }, 
	        {
	            "id": "OS", 
	            "module": "com.ketty.service.HostOS", 
	            "visible": "protected", 
	            "name": "OS", 
	            "path": "/alogic/host/OS", 
	            "log": "none", 
	            "note": "获取主机的OS信息"
	        }, 
	        {
	            "id": "CPUInfo", 
	            "module": "com.ketty.service.HostCPUInfo", 
	            "visible": "protected", 
	            "name": "CPUInfo", 
	            "path": "/alogic/host/CPUInfo", 
	            "log": "none", 
	            "note": "获取主机的CPU信息"
	        }
	    ], 
	    "code": "core.ok", 
	    "serial": "1441861176064c23FTbV"
	}

```

