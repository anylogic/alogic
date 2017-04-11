alogic-1.6.8
============

文档记录了alogic-1.6.8的更新日志。

### 1.6.8-SNAPSHOT [20170320 duanyy]
- 创建1.6.8-SNAPSHOT版本，开始1.6.8的开发

### 1.6.8.1 [20170320 duanyy]

#### alogic-common

- 修正com.alogic.metrics.xscript.AddMeasure的bug;
- 流处理基类AbstractHandler增加配置参数abandonWhenFull,当异步队列满的时候，可选择抛弃后续的数据;

### 1.6.8.2 [20170324 duanyy]

- 修正公式解析器中double值的比较问题;

### 1.6.8.3 [20170327 duanyy]

- 增加com.alogic.ha包，用于高可用控制;

### 1.6.8.4 [20170329 duanyy]

- xscript插件repeat现在可以对对象的属性进行循环处理;
- xsccrip插件lowercase,uppercase,substr,trim等将只取context变量，不取文档属性变量;
- xscript插件增加match插件，提供字符串匹配功能;

### 1.6.8.5 [20170331 duanyy]

- xscript：增加remove插件，用于删除工作文档的当前节点下指定子节点;
- alogic-vfs: 增加vfs-mkdir指令，用于在VFS中构建目录;

### 1.6.8.6 [20170406 duanyy]

- alogic-vfs:客户端可指定下载文件名，如果没有指定，则取路径之中的文件名.
- alogic-core:服务调用全局序列号采用随机64位数字(16进制).
- alogic-core:增加Options方法的实现

