package com.anysoft.loadbalance.impl;

import java.util.List;

import com.anysoft.loadbalance.AbstractLoadBalance;
import com.anysoft.loadbalance.Load;
import com.anysoft.util.Properties;


/**
 * 基于RoundRobin的LoadBalance
 * 
 * @author duanyy
 *
 * @param <load>
 * 
 * @version 1.5.3 [20141020 duanyy]
 * - 改造loadbalance模型
 */

public class RoundRobin<load extends Load> extends AbstractLoadBalance<load> {
	
	public RoundRobin(Properties props){
		super(props);
	}
	
	
	public load onSelect(String key,Properties props, List<load> loads) {
		load found = null;
		
		int size = loads.size();
		if (size > 0){
			found = loads.get(currentSelect % size);
			synchronized (this){
				currentSelect ++;
				if (currentSelect >= size){
					currentSelect = 0;
				}
			}
		}
		return found;
	}

	protected volatile int currentSelect = 0;
}
