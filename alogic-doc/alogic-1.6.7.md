alogic-1.6.7
============

文档记录了alogic-1.6.7的更新日志。

### 1.6.7.1 [20170116 duanyy]

- alogic-common:公式解析器(Parser)修改方法为protected，增加可定制性;
- alogic-jms:淘汰alogic-jms;
	
### 1.6.7.2 [20170117 duanyy]
	 
- alogic-commom:trace日志调用链中的调用次序采用xx.xx.xx.xx字符串模式;

### 1.6.7.3 [20170118 duanyy]
- alogic-common:新增com.alogic.tlog，替代com.alogic.tracer.log包;
- alogic-common:trace日志的时长单位改为ns;
- alogic-common:对tlog的开启开关进行了统一;

### 1.6.7.4 [20170118 duanyy]
- alogic-common:增加发送指标所需的xscript插件;
- alogic-common:淘汰com.anysoft.metrics包;
- alogic-core:服务耗时统计修改为ns;

### 1.6.7.5 [20170119 duanyy]
- alogic-common:JsonTools允许输入的json为空;
- alogic-vfs:增加VFS相关的XScript插件；

### 1.6.7.6 [20170125 duanyy] 
- alogic-common:Batch框架可以装入额外的CLASSPATH;

### 1.6.7.7 [20170126 duanyy]
- alogic-common:Properties增加loadFrom系列方法，用于从Json对象，Element节点，Element属性列表中装入变量列表
- alogic-core:删除缺省的log4j.properties配置
- alogic-common:xscript插件Set增加缺省值和引用模式

### 1.6.7.8 [20170128 duanyy]
- alogic-vfs:增加文件内容读取，保存，文件删除等xscript插件
- alogic-vfs:增加terminal框架，提供local和ssh两种实现

### 1.6.7.9 [20170201 duanyy]
- 采用SLF4j日志框架输出日志

### 1.6.7.10 [20170202 duanyy] 
- 修正tlog作为logger输出时的缓冲区并发问题

### 1.6.7.11 [20170202 duanyy] 
- alogic-vfs:SSH实现支持密码加密

### 1.6.7.12 [20170204 duanyy] 
- alogic-core:增加bizlog的handler

### 1.6.7.13 [20170206 duanyy]
- alogic-vfs:写文件接口增加permissions参数，以便在创建文件时指定文件的权限

### 1.6.7.14 [20170210 duanyy]
- alogic-vfs:修正文件比较和文件同步时信息填入问题
- alogic-vfs:支持多来源的check和sync操作

### 1.6.7.15 [20170216 duanyy]
- alogic-rpc:增加rpc框架
- alogic-core:为部分Message增加Content-Length字段，以便支持keep-alive
- alogic-core:增加bizlog.enable环境变量，以便关闭bizlog
- alogic-core:增加acm.enable环境变量，以便关闭ac控制器

### 1.6.7.16 [20170222 duanyy] 
- alogic-dbcp:增加tlog埋点
- alogic-dbcp:淘汰SQLTools工具类
- alogic-kvalue:增加info指令，以便于获取redis的状态信息

### 1.6.7.17 [20170223 duanyy] 
- alogic-rpc:设置kryo的类加载器，避免某些情况下kryo无法找到CLASS

### 1.6.7.18 [20170227 duanyy]
- alogic-vfs:修正SFtp的java.lang.NegativeArraySizeException异常

### 1.6.7.19 [20170228 duanyy]
- alogic-common:增加路径规范化工具Path
- alogic-common:为DefaultProperties增加装入功能

### 1.6.7.20 [20170302 duanyy]
- alogic-core:改造ServantManager模型,增加服务配置监控机制

### 1.6.7.21 [20170303 duanyy]
- alogic-common:tlog日志增加parameter字段，便于调用者记录个性化参数
- alogic-common:修改xscript的Get插件，可支持为空时忽略

### 1.6.7.22 [20170306 duanyy]
- alogic-common:xscrtip插件get不再将当前文档节点的属性作为变量
- alogic-common:xscript插件repeat当jsonPath语法错误或者节点不存在时，不再抛出异常

### 1.6.7.23 [20170307 duanyy]
- alogic-dbcp:增加开关，为了提高效率，可以不在交付Connection之前进行有效性测试
