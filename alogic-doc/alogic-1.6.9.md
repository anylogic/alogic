alogic-1.6.9
============

文档记录了alogic-1.6.9的更新日志。

### 1.6.9-SNAPSHOT [20170521 duanyy]
- 创建1.6.9-SNAPSHOT版本，开始1.6.9的开发;
- alogic-common:修正location插件的bug，同时支持xml和json的定位;
- alogic-common:修改logiclet插件的加载次序,便于指令的重载;


### 1.6.9.1 [20170516 duanyy]
- 修复部分插件由于使用新的文档模型产生的兼容性问题;

### 1.6.9.2 [20170525 duanyy]
- alogic-doer:改造TaskCenter模型，以便提供分布式任务处理支持;
- alogic-dbcp:修改DBTools工具，提供简单的ORM框架;

### 1.6.9.3 [20170602 duanyy]
- alogic-common:xscipt插件block，在异常处理的时候，输出异常的id和原因信息;
- alogic-addons:修正Query插件，在查询记录为空时的异常问题;
- alogic-common:修正tlog的全局序列号不规范问题;
- alogic-common:XsObject类增加是否为空的方法;
- alogic-doer:增加相应的查询服务及启动引导类;
- alogic-vfs:增加移动文件的方法;

### 1.6.9.4 [20170615 duanyy]
- alogic-vfs：统一各实现的文件名匹配规则为正则表达式匹配;

### 1.6.9.5 [20170624 duanyy]
- alogic-common:修正流处理框架中异步处理时的数据丢失问题;

### 1.6.9.6 [20170706 duanyy]
- alogic-vfs:增加文件移动和文件拷贝脚本;
- alogic-common:XmlObject避免属性值为空的情况;

### 1.6.9.7 [20170731 duanyy] 
- alogic-common:FromSettings和FromEnv支持refer和raw的功能;
- alogic-common:Now增加模板输出功能;
- alogic-common:RRModelManager修正多实例下的并发问题;
- alogic-dbcp:DBTools增加AutoCommit相关的操作;
- alogic-dbcp:增加强制读写分离功能，应对数据中间层之类的场景;
- alogic-dbcp:可自动为Connection设置autocommit属性;

### 1.6.9.8 [20170821 duanyy]
- alogic-common:tlog增加keyword字段;
- alogic-core:服务上下文增加keyword关键字，和tlog对接;
- alogic-doer:任务id修改为18位数字(当前时间戳+随机数字);
