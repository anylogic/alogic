package com.anysoft.loadbalance.impl;

import java.util.List;
import java.util.Random;

import com.anysoft.loadbalance.AbstractLoadBalance;
import com.anysoft.loadbalance.Load;
import com.anysoft.util.Properties;

/**
 * 基于主键Hash的LoadBalance
 * 
 * @author duanyy
 *
 * @param <load>
 * 
 * @version 1.5.3 [20141020 duanyy]
 * - 改造loadbalance模型
 */
public class Hash<load extends Load> extends AbstractLoadBalance<load> {

	public Hash(Properties props){
		super(props);
	}	
	
	
	public load onSelect(String key,Properties props, List<load> loads) {
		load found = null;
		
		int size = loads.size();
		if (size > 0){
			int hashcode = 0;
			if (key == null || key.length() <= 0){
				//当没有传入Key的时候，同Rand模式
				hashcode = r.nextInt(size) % size;	
			}else{			
				hashcode = key.hashCode();
			}
			found = loads.get((hashcode & Integer.MAX_VALUE) % size);
		}
		
		return found;
	}
	
	public static Random r = new Random();
}
