package com.logicbus.dbcp.util;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.SimpleCounter;

/**
 * 连接池统计工具
 * 
 * @author duanyy
 * 
 * @since 1.2.9
 *
 * @version 1.2.9.1 [20141017 duanyy]
 *  - 从Counter模型中继承
 */
public class ConnectionPoolStat extends SimpleCounter{

	public ConnectionPoolStat(Properties p) {
		super(p);
	}
	
	public long getStatCycle(Properties p){
		return PropertiesConstants.getLong(p, "dbcp.stat.cycle", 5 * 60 * 1000L);
	}		
}
