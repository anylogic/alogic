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

### 1.6.11.17 [20180209 duanyy]

- alogic-dbcp:支持SQL预处理;

### 1.6.11.18 [20180211 duanyy]

- alogic-auth:SessionManager增加设定cookie的有效期;
- alogic-common:优化事件处理;
- alogic-core:HybirdAccessController增加scope的支持;

### 1.6.11.19 [20180212 duanyy]

- alogic-common:增加if-equal和if-n-equal指令;

### 1.6.11.20 [20180223 duanyy]

- alogic-common:修正事件加载器FromClasspath的异常;
- alogic-kvalue:缓存实现的间隔符由$更改为#;
- alogic-kvalue:修正缓存对象id的问题;
- alogic-auth:修改GetTokenInfo服务实现的app值的缺省id;
- alogic-kvalue:缓存的idtable功能可选,默认管理;

### 1.6.11.21 [20180227 duanyy]

- alogic-bundle:增加alogic-bundle工程，用于快速使用alogic;

### 1.6.11.22 [20180313 duanyy]

- alogic-auth:优化SessionAccessController,匿名用户可以访问public服务;
- alogic-auth:扩展AuthenticationHandler接口;
- alogic-auth:优化Filter对URL的支持;
- alogic-auth:Token验证服务的IP绑定功能可配置;
- alogic-bundle:优化配置参数;
- alogic-core:JsonMessage可以按指定的jsonpath路径输出文档;
- alogic-vfs:修正HybirdBlobManager的主备规则;

### 1.6.11.23 [20180320 duanyy]

- alogic-auth:修正某些不可配置的参数名;
- alogic-rpc:Gateway增加本地服务的转调功能;

### 1.6.11.24 [20180323 duanyy]

- alogic-dbcp:增加db-select插件，用于选择一行记录，并插入到变量集;

### 1.6.11.25 [20180323 duanyy]

- alogic-common:增加EventServer接口;

### 1.6.11.26 [20180328 duanyy]

- alogic-common:增加redirect框架;
- alogic-common:增加 Event序列化接口EventSerializer;
- alogic-common:EventSender可以向指定的上下文EventHandler发送事件;
- alogic-common:增加事件处理插件Bridge，用于向指定的事件服务器发送事件;

### 1.6.11.27 [20180417 duanyy]

- alogic-auth:修正SessionManager获取cookies的空指针问题;
- alogic-common:修正事件处理器Bridge的初始化问题;
- alogic-common:增加xscript的函数相关的插件func-declare,func-call,func-callback;
- alogic-common:xscript的crypt-en插件允许Key为空;
- alogic-common:增加HmacSHA1的Coder;
- alogic-dbcp:数据库插件增加debug开关,以便输出SQL调试信息;
- alogic-dbcp:增加db-keyvalues插件;

### 1.6.11.28 [20180420 duanyy]

- alogic-common:增加array-string和set-multi插件;

### 1.6.11.29 [20180510 duanyy]

- alogic-common:增加cache相关的操作插件;
- alogic-common:cache框架增加on-load事件的脚本处理功能;

### 1.6.11.30 [20180514 duanyy]

- alogic-auth:SessionManager增加cookies的操作接口;
- alogic-common:增加全局xscript脚本函数库;

### 1.6.11.31 [20180522 duanyy]

- alogic-common:obj插件
- alogic-kvalue:优化缓存中set的ttl机制;
- alogic-lucene:完善lucene组件;

### 1.6.11.32 [20180529 duanyy]

- alogic-common:增加ScriptCache机制；
- alogic-common:增加load,eval脚本插件;
- alogic-lucene:增加ik实现代码;
- alogic-common:增加lock和counter脚本插件;
- alogic-together:增加redirect服务;

### 1.6.11.33 [20180601 duanyy]

- alogic-common:修正counter处理大的整数时的bug;

### 1.6.11.34 [20180606 duanyy]

- alogic-common:增加字符串处理插件sp;
- alogic-common:增加集合处理插件array-set;
- alogic-lucene:优化字典的管理，支持扩展字典的加载;
- alogic-lucene:支持范围查询;

### 1.6.11.35 [20180612 duanyy]

- alogic-report:增加report工程，用于制作excel报表;

### 1.6.11.36 [20180613 duanyy]

- alogic-common:增加plus,mul,div等xscript插件;
- alogic-dbcp:sql预处理器支持transform机制;
- alogic-lucene:增加sort功能;
- alogic-common:公式解析器的变量id支持$字符;

### 1.6.11.37 [20180619 duanyy]

- alogic-common:Settings增加特定工具集的框架；
- alogic-common:增加资源分享框架;

### 1.6.11.38 [20180615 duanyy]

- alogic-common:增加xml文档操作相关插件;

### 1.6.11.39 [20180628 duanyy]

- alogic-auth:SSO客户端处理器的远程调用部分增加同步锁;
- alogic-rpc:客户端增加x-alogic-ac参数，以便服务端识别acGroup;

### 1.6.11.40 [20180704 duanyy]

- alogic:logback版本改为1.2.3;
- alogic-auth:SessionManager可以设置cookie到指定的domain;
- alogic-together:DownloadTogetherServant支持指定的content-type;

### 1.6.11.41 [20180705 duanyy] 

- alogic-dbcp:修正keyvalues插件的bug;

### 1.6.11.42 [20180706 duanyy]

- alogic-common:xscript的set语句支持动态id;

### 1.6.11.43 [20180708 duanyy]

- alogic-common:cache模型中增加cache-hgetall指令;
- alogic-common:cache模型中cache-hset,cache-sadd等支持raw模式;
- alogic-common:xscript的get支持raw模式; 
- alogic-common:xscript增加array-set-list指令;

### 1.6.11.44 [20180713 duanyy]

- alogic-common:xscript的eval插件在脚本编译时采用Settings作为变量集;
- alogic-common:增加LocalLock工具类;
- alogic-common:webloader增加webcontext.vroot变量;

### 1.6.11.45 [20180722 duanyy]
- alogic-auth:sso的ClientSideHandler增加从cookies获取token的模式;
- alogic-common:缓存框架增加nocache模式;
- alogic-common:Redirector只是contextPath;
- alogic-dbcp:增加HotSQLLoader实现;
- alogic-core:Context增加getHostDomain方法;
- alogic-together:服务内置变量增加$url,$hostdomain;

### 1.6.11.46 [20180726 duanyy]
- alogic-common:修正XscriptCache的问题;
- alogic-lucene:修改QueryBuilder,build时增加Analyzer上下文;
- alogic-lucene:增加ByMatch,ByTerms等QueryBuilder实现;

### 1.6.11.47 [20180806 duanyy] 
- alogic-core:修正MixedAccessController的配置参数问题;
- alogic-dbcp:conn插件支持已有上层对象;

### 1.6.11.48 [20180807 duanyy]
- 优化服务和资源的缓存相关的http控制头;

### 1.6.11.49 [20180808 duanyy]
- alogic-vfs:修正下载中文名的乱码问题;

### 1.6.11.50 [20180808 duanyy]
- alogic-rpc:Gateway增加form数据的拦截模式;
- alogic-rpc:优化Gateway的缓存处理;

### 1.6.11.51 [20180810 duanyy]
- alogic-common:xscript的set指令增加raw模式;

### 1.6.11.52 [20180816 duanyy]
- alogic-lucene:增加删除文档的xscript插件;

### 1.6.11.53 [20180817 duanyy]
- alogic-vfs:增加基于amazon s3的BlobManager实现;

### 1.6.11.54 [20180822 duanyy]
- alogic-common:增加所生成证书的keyUsage和extKeyUsage的设置;

### 1.6.11.55 [20180822 duanyy]
- alogic-common:增加获取证书序列号功能;

### 1.6.11.56 [20180823 duanyy]
- alogic-common:证书的序列号可定制;

### 1.6.11.57 [20180828 duanyy]
- alogic-common:增加regex-match插件;
- alogic-auth:SessionAccessController增加浏览器会话id的传递;

### 1.6.11.58 [20180829 duanyy]
- alogic-common:增加树相关的操作，如数组转换为树，树遍历等;
- alogic-dbcp:修正on-load事件处理问题;
- alogic-common:修正on-load事件处理问题;
- alogic-kvalue:修正on-load事件处理问题

