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