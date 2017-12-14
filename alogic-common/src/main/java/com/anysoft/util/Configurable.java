package com.anysoft.util;

/**
 * 可配置
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public interface Configurable {
	/**
	 * 根据环境变量配置
	 * 
	 * @param p 环境变量
	 */
	public void configure(Properties p);
}
