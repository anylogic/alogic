package com.alogic.metrics.stream;

import com.alogic.metrics.Fragment;

/**
 * 指标收集器
 * 
 * @author yyduan
 *
 * @since 1.6.6.13
 *
 */
public interface MetricsCollector {
	
	/**
	 * 汇聚指标片段
	 * @param f 指标片段
	 */
	public void metricsIncr(Fragment f);
}
