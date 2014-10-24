package com.logicbus.backend;

import com.anysoft.util.*;


/**
 * 服务统计信息
 * 
 * @author duanyy
 * 
 * @version 1.2.4 [20140703 duanyy]
 * 	- 增加workingCnt和idleCnt统计信息
 * 
 * @version 1.2.8.2 [20141014 duanyy]
 *  - 不再收集缓冲池的信息
 *  - 实现Reportable
 *  - 简化ServantStat模型
 *  
 * @version 1.2.9.2 [20141017 duanyy]
 *  - 从Counter模型继承
 */
public class ServantStat extends SimpleCounter {

	public ServantStat(Properties p) {
		super(p);
	}

	public long getStatCycle(Properties p){
		return PropertiesConstants.getLong(p, "servant.stat.cycle", 5 * 60 * 1000L);
	}	
}
