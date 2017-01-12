package com.alogic.metrics.stream;

/**
 * 可报告指标接口
 * 
 * @author duanyy
 * 
 * @since 1.2.8.2
 *
 * @since 1.6.6.13
 *
 */
public interface MetricsReportable {
	
	/**
	 * 报告指标数据到collector
	 * @param collector collector
	 */
	public void report(MetricsCollector collector);
}
