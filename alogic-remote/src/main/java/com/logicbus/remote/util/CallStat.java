package com.logicbus.remote.util;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.SimpleCounter;

/**
 * 统计模型
 * 
 * @author duanyy
 * 
 * @since 1.2.9.1
 * 
 */
public class CallStat extends SimpleCounter {

	public CallStat(Properties p) {
		super(p);
	}

	public long getStatCycle(Properties p){
		return PropertiesConstants.getLong(p, "call.stat.cycle", 5 * 60 * 1000L);
	}		
}
