package com.anysoft.loadbalance;

import com.anysoft.util.Counter;


/**
 * 负载统计
 * 
 * @author duanyy
 *
 */
public interface LoadCounter extends Counter {
	
	/**
	 * 获取使用次数（最近的一段周期）
	 * 
	 * @return
	 */
	public long getTimes();
	
	/**
	 * 获取平均使用时长（最近的一段周期）
	 * @return
	 */
	public double getDuration();
	
	/**
	 * 根据计数信息判断是否有效
	 * @return
	 */
	public boolean isValid();
}
