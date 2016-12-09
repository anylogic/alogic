alogic-1.6.6
============

文档记录了alogic-1.6.6的更新日志。

- 1.6.6.1 [20160823 duanyy]
	 - 修改xscript插件template，支持extend模式
	 - 增加xscript插件getAsJson,从变量中解析出Json对象，并扩展或添加到当前文档节点
	 - 增加xscript插件setAsJson,将当前节点的Json对象转换为字符串，并设置为上下文变量
	 
- 1.6.6.2 [20160826 duanyy] 
 	 - alogic-kvalue:对于SortedSet的rangeByScore和rangeByScoreWithScores接口增加分页功能

- 1.6.6.3 [20160907 duanyy] 
	 - alogic-kvalue:修正SortedSetTool进行zrangebyscore查询的语法错误问题.
	 
- 1.6.6.4 [20160907 duanyy] 
	 - alogic-kvalue:修正SortedSetTool进行zrangebyscore查询的语法错误问题.	 
	 
- 1.6.6.5 [20161121 duanyy] 
	- alogic-common:Watcher接口增加allChanged方法，以便通知Watcher所有对象已经改变
	
- 1.6.6.6 [20161121 duanyy]
	- alogic-kvalue:增加对redis指令ZRANGEBYLEX,ZLEXCOUNT,ZREMRANGEBYLEX的支持，该指令支持通过字典区间来操作zset类型的数据，适合redis-2.8.9版本
	
- 1.6.6.7 [20161122 duanyy]
	- alogic-kvalue:修正bug
	
- 1.6.6.8 [20161208 duanyy]
	- alogic-common:增加com.alogic.naming，以命名服务的形式提供全局对象的配置框架，用来替代com.anysoft.context
	- alogic-common:增加com.alogic.pool,全新的对象管理池的实现，用来替代com.anysoft.pool
	
- 1.6.6.9 [20161209 duanyy]
	- alogic-core:QueuedServantPool2从新的框架下继承
	- alogic-core:淘汰QueuedServantPool
	- alogic-dbcp:缓冲池实现采用新的框架
	- alogic-kvalue:连接池实现采用新的框架

	