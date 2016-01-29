package com.logicbus.backend;

import com.anysoft.util.*;


/**
 * 服务统计信息
 * 
 * @author duanyy
 * 
 * @version 1.2.4 [20140703 duanyy] <br>
 * 	- 增加workingCnt和idleCnt统计信息 <br>
 * 
 * @version 1.2.8.2 [20141014 duanyy] <br>
 *  - 不再收集缓冲池的信息 <br>
 *  - 实现Reportable <br>
 *  - 简化ServantStat模型 <br>
 *  
 * @version 1.2.9.2 [20141017 duanyy] <br>
 *  - 从Counter模型继承 <br>
 *  
 * @version 1.6.4.31 [20160128 duanyy] <br>
 * - 增加活跃度和健康度接口 <br>
 * - 增加可配置性 <br>
 */
public class ServantStat extends SimpleCounter {

	public ServantStat(Properties p) {
		super(p);
	}

	@Override
	public long getStatCycle(Properties p){
		return PropertiesConstants.getLong(p, "servant.stat.cycle", 5 * 60 * 1000L);
	}	
}
