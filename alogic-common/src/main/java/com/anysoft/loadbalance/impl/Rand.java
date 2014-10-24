package com.anysoft.loadbalance.impl;

import java.util.List;
import java.util.Random;

import com.anysoft.loadbalance.AbstractLoadBalance;
import com.anysoft.loadbalance.Load;
import com.anysoft.util.Properties;

/**
 * 基于随机算法的LoadBalance
 * 
 * @author duanyy
 *
 * @param <load>
 * 
 * @version 1.5.3 [20141020 duanyy]
 * - 改造loadbalance模型
 */
public class Rand<load extends Load> extends AbstractLoadBalance<load> {

	public Rand(Properties props){
		super(props);
	}
	
	
	public load onSelect(String key, Properties props, List<load> loads) {
		load found = null;
		int size = loads.size();
		if (size > 0){	
			found = loads.get(r.nextInt(size) % size);
		}
		return found;
	}

	public static Random r = new Random();
}
