package com.anysoft.metrics.core;


/**
 * 指标收集器
 * 
 * @author duanyy
 * @since 1.2.8
 */
public interface MetricsCollector {
	
	/**
	 * 指标叠加
	 * 
	 * @param id
	 * @param fragment
	 */
	public void metricsIncr(Fragment fragment);
}
