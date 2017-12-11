alogic-1.6.10
=============

文档记录了alogic-1.6.10的更新日志。

### 1.6.10-SNAPSHOT [20170521 duanyy]
- 创建1.6.10-SNAPSHOT版本，开始1.6.10的开发

### 1.6.10.1 [20170910 duanyy]
- alogic-common:AddDemension,AddMeasure的ID参数改为可计算参数;
- alogic-rpc:修正httpclient连接的“failed to respond”异常;
- alogic-addons:增加SQL语句的scan指令;
- alogic-addons:DB和Cache操作增加相应的前缀;

### 1.6.10.2 [20170925 duanyy]
- alogic-common:优化async指令，采用私有线程池，可指定线程池大小，并且可关闭线程池。
- alogic-common:PropertiesConstants增加transform系列方法。
- alogic-common:优化foreach,repeat指令，增加异步执行功能。
- alogic-common:增加rand指令，用于生成一定范围内的随机数。

### 1.6.10.3 [20171009 duanyy]
- alogic-rpc:httpClient增加PUT,GET,DELETE,HEAD,OPTIONS,TRACE,PATCH等http方法;
- alogic-rpc:增加基于Client的Naming框架;
- algoic-rpc:增加基于Client的xscript脚本插件;

### 1.6.10.4 [20171017 duanyy] 
- alogic-kvalue:RedisPool优化密码取值功能;

### 1.6.10.5 [20171102 duanyy]
- 修正部分类的注释;
- alogic-common:增加基于HmacSHA256的Coder;
- alogic-dbcp:增加数据库操作相关的xscript插件,作为alogic-addons中idu的替代方案;
- alogic-cache:增加缓存相关的xscript插件,作为alogic-addons中idu的替代方案;
- alogic-zk:增加zk相关的模块;

### 1.6.10.6 [20171107 duanyy]
- alogic-addons:重写ACM模型，增加签名，密码等服务验证功能;
- alogic-common:增加HmacSHA256和SHA256编码器;
- alogic-core:修改对调用者实际IP和代理IP的取值规则;
- alogic-rpc:增加Http调用请求级别的Filter和Client级别的Customizer，并增加多种插件;
- algoic-vfs:vfs比较和同步增加路径的白名单和黑名单功能;
- alogic-zk:Zookeeper版本由3.4.6升级到3.4.10;

### 1.6.10.7 [20171115 duanyy]
- alogic-addons:优化ACM模型，在ACM统计中增加denied的服务调用个数的字段;

### 1.6.10.8 [20171119 duanyy]
- alogic-common:Properties在装入xml文件时，支持设置空字符串(字符串长度为0);
- alogic-common:增加SDA加载框架，用于加载私密配置数据;
- alogic-dbcp:支持实时从SDA中获取数据;
- alogic-kvalue:支持实时从SDA中获取数据;
- alogic-rpc:支持实时从SDA中获取数据;
- alogic-common:淘汰Reload框架;

### 1.6.10.9 [20171124 duanyy]
- alogic-addons:Signature和RSA验证的签名文本中的URL更改为URI;
- alogic-common:增加IfTrue,IfFalse,IfExist,IfNotExist等xscript插件;
- alogic-common:增加crypto相关的xscript插件;
- alogic-core:规范URI和URL取值;

### 1.6.10.10 [20171203 duanyy]
- alogic-common:增加HashObject和SetObject基础接口;
- alogic-auth:增加alogic-auth模块，用于登录验证;

### 1.6.10.10 [20171206 duanyy]
- 完善alogic-auth模块;

### 1.6.10.10 [20171211 duanyy]
- 迁移alogic-addons中的ac模块到alogic-auth中;
- 完善alogic-auth模块;

### 1.6.10.11 [20171211 duanyy]
- 淘汰alogic-examples工程;
- 淘汰服务的Argument配置机制;