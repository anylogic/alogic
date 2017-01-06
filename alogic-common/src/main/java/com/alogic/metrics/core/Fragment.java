package com.alogic.metrics.core;

import com.anysoft.stream.Flowable;
import com.anysoft.util.JsonSerializer;

/**
 * 指标片段
 * 
 * @author duanyy
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
}
