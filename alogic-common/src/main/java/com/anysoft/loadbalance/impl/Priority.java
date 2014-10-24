package com.anysoft.loadbalance.impl;

import java.util.List;

import com.anysoft.loadbalance.AbstractLoadBalance;
import com.anysoft.loadbalance.Load;
import com.anysoft.util.Properties;


/**
 * 基于优先级的LoadBalance
 * 
 * @author duanyy
 *
 * @param <load>
 * 
 * @version 1.5.3 [20141020 duanyy]
 * - 改造loadbalance模型
 */
public class Priority<load extends Load> extends AbstractLoadBalance<load> {

	public Priority(Properties props){
		super(props);
	}	
	
	
	public load onSelect(String key,Properties props, List<load> loads) {
		load found = null;
		
		int size = loads.size();
		if (size > 0){
			int highestIndex = 0;
			int highestPriority = 0;
			for (int i = 0 ; i < size; i ++){
				int _p = loads.get(i).getPriority();
				if (_p > highestPriority){
					highestIndex = i;
					highestPriority = _p;
				}
			}
			found = loads.get(highestIndex);
		}
		return found;
	}

}
