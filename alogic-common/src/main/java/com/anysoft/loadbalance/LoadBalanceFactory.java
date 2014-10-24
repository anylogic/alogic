package com.anysoft.loadbalance;

import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;

/**
 * 工厂类
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
public class LoadBalanceFactory<load extends Load> extends Factory<LoadBalance<load>> {
	public String getClassName(String _module) throws BaseException{
		if (_module.indexOf('.') < 0){
			return "com.anysoft.loadbalance.impl." + _module;
		}
		return _module;
	}
}
