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