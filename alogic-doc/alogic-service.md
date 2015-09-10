alogic内置服务接口文档
==================

本文档记录了alogic内置服务接口信息。

本项目的服务接口基于alogic服务框架。

## 服务调用方法

通过服务端提供的REST服务进行调用，地址为:
```
	http://<ip>:<port>/services/服务路径>[?<参数名>=<参数值>&[<参数名>=<参数值>]]
```

其中服务路径可通过管理服务进行查询或者参见接口文档。
```
	http://<ip>:<port>/services
```

## 服务输入协议

接受下列两种形式的服务参数：
* Get方法下的Query串；
* Post方式下的Form字段

如有需要，服务端可接收POST过来的二进制或字符流文档（视具体服务而定）

## 服务输出协议

服务的输出形式为JSON文本。

服务框架将输出下列信息：

| 编号 | 代码 | 类型 | 名称 | 说明 |
| ---- | ---- | ---- | ---- | ---- |
| 1 | duration | Long | 本次调用的时长(ms) | 服务框架中服务开始和服务结束的耗时（不包括网络传输耗时，供参考）|
| 2 | host | String | 提供服务的主机和端口 | |
| 3 | code | String | 返回代码 | |
| 4 | reason | String | 错误原因 | 对返回代码的解释 |
| 5 | serial | String | 全局序列号 | 本次服务调用的全局序列号 |

例如，采用JSON协议时，返回如下：
```
{
  "duration": "13",
  "host": "0:0:0:0:0:0:0:1:9000",
  "reason": "It is successful",
  "code": "core.ok",
  "serial": "1436772822305OqmICGD"
}
```

在某些特殊情况下（如无法确定输出协议，无法找到服务等），服务端采用http错误码（固定为404）来进行返回。

## 使用
alogic提供的内置服务封装在alogic-addons工程中,可在服务目录配置文件中通过下列方式引入：

```xml
	<catalog 
		module="com.logicbus.models.servant.impl.XMLResourceServantCatalog" 
		xrc="/com/logicbus/service/servant.addons.xml"
		class="com.logicbus.models.servant.impl.XMLResourceServantCatalog"
	/>
```

## 服务列表

包含下列服务：
* 核心服务
	* 服务
		* [查询服务信息](alogic-service/core/ServiceDetailQuery.md)
		* [查询服务目录](alogic-service/core/ServiceQuery.md)
		* [查询服务列表](alogic-service/core/ServiceList.md)
		* [重新装载服务线程池](alogic-service/core/ServiceReload.md)
		* [暂停服务](alogic-service/core/ServicePause.md)
		* [恢复服务](alogic-service/core/ServiceResume.md)
	* 访问控制
		* [查询访问控制器的统计信息](alogic-service/core/AclQuery.md)
	* 日志
		* [日志处理报告](alogic-service/core/log/Report.md)
		* [暂停日志处理](alogic-service/core/log/Pause.md)
		* [恢复日志处理](alogic-service/core/log/Resume.md)
	* 指标
		* [指标处理报告](alogic-service/core/metrics/Report.md)
		* [暂停指标处理](alogic-service/core/metrics/Pause.md)
		* [恢复指标处理](alogic-service/core/metrics/Resume.md)	
* 公共组件
	* 缓存
		* [配置源报告](alogic-service/component/cache/Source.md)
		* [活跃缓存列表](alogic-service/component/cache/List.md)
		* [查询指定缓存信息](alogic-service/component/cache/Query.md)
		* [查询缓存对象信息](alogic-service/component/cache/CachedObject.md)
		* [过期缓存](alogic-service/component/cache/Expire.md)
	* 数据库连接池
		* [配置源报告](alogic-service/component/dbcp/Source.md)
		* [活跃数据库连接池列表](alogic-service/component/dbcp/List.md)
		* [查询数据库连接池信息](alogic-service/component/dbcp/Query.md)
	* 远程调用
	* kvalue
	* 全局序列
	* blob
	* 定时调度
	* 全文检索(开发中)