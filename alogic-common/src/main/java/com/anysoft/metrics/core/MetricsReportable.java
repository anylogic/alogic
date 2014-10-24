package com.anysoft.metrics.core;


/**
 * 可报告指标接口
 * 
 * @author duanyy
 * 
 * @since 1.2.8.2
 *
 */
public interface MetricsReportable {
	
	/**
	 * 报告指标数据到collector
	 * @param frag
	 */
	public void report(MetricsCollector collector);
}
