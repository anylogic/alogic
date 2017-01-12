package com.alogic.metrics;

import com.anysoft.stream.Flowable;
import com.anysoft.util.JsonSerializer;

/**
 * 指标片段
 * 
 * @author duanyy
 *
 * @since 1.6.6.13
 *
 */
public interface Fragment extends JsonSerializer,Flowable{

	/**
	 * 汇聚方法
	 * @author duanyy
	 *
	 */
	public static enum Method {
		lst,
		avg,
		max,
		min,
		sum
	};
	
	/**
	 * 数据类型
	 * @author duanyy
	 *
	 */
	public static enum DataType {
		/**
		 * double类型
		 */
		D,
		/**
		 * long类型
		 */
		L,
		/**
		 * string类型
		 */
		S
	};	
	
	/**
	 * to get id of the metrics
	 * @return id
	 */
	public String id();
	
	/**
	 * 获取Fragment的类型
	 * 
	 * <p>Fragment类型包括:
	 * <li>metrics</li>:系统运行的指标；
	 * <li>info</li>:系统的信息
	 * <li>alarm</li>:告警信息
	 * @return 类型
	 */
	public String type();
	
	/**
	 * 获取时间戳
	 * @return timestamp
	 */
	public long getTimestamp();
	
	/**
	 * 获取维度列表
	 * @return 维度列表
	 */
	public Dimensions getDimensions();
	
	/**
	 * 获取量度列表
	 * @return 量度列表
	 */
	public Measures getMeasures();
	
	/**
	 * 指标汇聚
	 * @param other 另一个指标
	 * @return 新的指标
	 */
	public Fragment incr(Fragment other);
}
