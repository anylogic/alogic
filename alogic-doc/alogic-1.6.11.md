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

### 1.6.11.3 [20171219 duanyy]

- alogic-auth:集群模式可通过开关开启;
- alogic-common:流处理框架可根据数据来设置同步/异步处理模式;
- alogic-common:增加本地路径下事件处理器的装载器;
- alogic-together:增加2.0版本的实现;

### 1.6.11.4 [20171222 duanyy]

- alogic-auth:SessionAccessController将当前用户id写入到上下文；
- alogic-common:Loader框架增加Hot实现，用于热部署场景下的加载;
- alogic-common:tlog框架增加服务统计插件;
- alogic-rpc:增加gateway功能；

### 1.6.11.5 [20171227 duanyy]

- alogic-common:新增uid框架，作为全局序列号的实现，替代alogic-seq

### 1.6.11.6 [20180103 duanyy]

- alogic-seq:设置为deprecated
- alogic-cache:设置为deprecated
- alogic-common:新增cache框架，用于替代alogic-cache
- alogic-addons:idu相关的设置为deprecated

### 1.6.11.7 [20180107 duanyy]

- alogic-auth:优化Session管理

### 1.6.11.8 [20180109 duanyy]

- alogic-common:优化缓存相关的xscript插件;
- alogic-dbcp:优化缓存相关的SQLLoader;

### 1.6.11.9 [20180111 duanyy]

- alogic-common:增加证书的创建和签发功能;
- alogic-common:优化缓存相关的xscript插件;

### 1.6.11.10 [20180116 duanyy]

- alogic-auth:增加可授权签名模式的服务端插件;
- alogic-rpc:增加可授权签名模式的客户端插件;
- alogic-common:增加cache-sexist的缓存操作插件;
- alogic-common:x509证书采用Bouncy Castle的类库来生成;

### 1.6.11.11 [20180123 duanyy]

- alogic-blob:淘汰alogic-blob,采用alogic-vfs功能替代;
- alogic-addons:淘汰idu相关的服务框架;
- alogic-seq:淘汰alogic-seq,采用alogic-common中的uid框架替代;

### 1.6.11.12 [20180123 duanyy]

- alogic-core:http响应的缓存属性改成由服务来个性化控制;
- alogic-together:增加下载类服务的组装功能;

### 1.6.11.13 [20180125 duanyy]

- alogic-cache:淘汰alogic-cache工程;
- alogic-kvalue:增加基于kvalue的分布式缓存实现;

### 1.6.11.14 [20180129 duanyy]

- alogic-auth:优化接口;
- alogic-auth:增加基于cookies的Session管理方式;


### 1.6.11.15 [20180206 duanyy]

- alogic-common:配置类的框架增加scope支持;
- alogic-dbcp:修正Preprocessor的并发性问题;

### 1.6.11.16 [20180207 duanyy]

- alogic-common:增加事件脚本的CopyProperties插件;
- alogic-common:优化事件框架的Script插件;
- alogic-common:优化incr,decr插件;
- alogic-rpc:优化ip的forwarded传递;
- alogic-rpc:ClusterManager框架增加scope支持;
