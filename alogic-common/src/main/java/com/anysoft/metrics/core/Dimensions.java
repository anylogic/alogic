package com.anysoft.metrics.core;

import com.anysoft.util.JsonSerializer;
import com.anysoft.util.XmlSerializer;

/**
 * 指标的维度集
 * 
 * @author duanyy
 * 
 * @since 1.2.8
 */
public interface Dimensions extends XmlSerializer,JsonSerializer {
	
	/**
	 * 获取维度的个数
	 * @return count of dimensions
	 */
	public int count();
	
	/**
	 * 从左边增加一个或多个维度
	 * 
	 * @param dims
	 * @return Dimensions
	 */
	public Dimensions lpush(String...dims);
	
	/**
	 * 从右边增加一个或多个维度
	 * @param dims
	 * @return Dimensions
	 */
	public Dimensions rpush(String...dims);
	
	/**
	 * 获取子维度集
	 * @param start
	 * @param count
	 * @return Dimensions
	 */ 
	public Dimensions sub(int start,int count);
	
	/**
	 * 获取所有的维度
	 * @return all dimensions in strings
	 */
	public String [] get();
	
	/**
	 * 获取指定位置的维度
	 * @param idx
	 * @return dimension
	 */
	public String get(int idx);
	
	/**
	 * 获取一个到多个维度
	 * @param start
	 * @param count
	 * @return dimesions
	 */
	public String[] get(int start,int count);
}
