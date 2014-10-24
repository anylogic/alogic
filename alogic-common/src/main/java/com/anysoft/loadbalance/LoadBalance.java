package com.anysoft.loadbalance;

import java.util.List;

import com.anysoft.util.Properties;

/**
 * LoadBalance接口
 * 
 * @author duanyy
 *
 * @param <load>
 * 
 * @since 1.2.0
 * 
 * @version 1.5.3 [20141120 duanyy]
 * - 改造loadbalance模型
 * 
 */
public interface LoadBalance<load extends Load> {
	
	public load select(String key,Properties props,List<load> loads);
}
