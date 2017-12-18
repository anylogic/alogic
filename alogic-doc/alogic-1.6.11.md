alogic-1.6.11
=============

文档记录了alogic-1.6.11的更新日志。

### 1.6.11-SNAPSHOT [20171214 duanyy]
- 创建1.6.11-SNAPSHOT版本，开始1.6.11的开发

### 1.6.11.1 [20171215 duanyy]

- alogic-core:为内置服务增加acGroupId和privilege属性;
- alogic-addons:为内置服务增加acGroupId和privilege属性;
- alogic-auth:增加基于访问Session的访问控制器;
- alogic-auth:AuthenticationHandler接口修正退出登录方法;
- alogic-auth:Principal增加获取登录id的方法;
- alogic-common:Store框架增加有效期的判定;
- alogic-common:Selector增加final属性;

### 1.6.11.2 [20171218 duanyy]

- alogic-auth:AuthGuard在重定向登录页面的时候，支持集群负载均衡;
- alogic-common:事件处理框架增加xscript相关插件;
- alogic-core:将事件处理框架作为内置组件;