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
	 * @return
	 */
	public int count();
	
	/**
	 * 从左边增加一个或多个维度
	 * 
	 * @param dims
	 * @return
	 */
	public Dimensions lpush(String...dims);
	
	/**
	 * 从右边增加一个或多个维度
	 * @param dims
	 * @return
	 */
	public Dimensions rpush(String...dims);
	
	/**
	 * 获取子维度集
	 * @param start
	 * @param count
	 * @return
	 */
	public Dimensions sub(int start,int count);
	
	/**
	 * 获取所有的维度
	 * @return
	 */
	public String [] get();
	
	/**
	 * 获取指定位置的维度
	 * @param idx
	 * @return
	 */
	public String get(int idx);
	
	/**
	 * 获取一个到多个维度
	 * @param start
	 * @param count
	 * @return
	 */
	public String[] get(int start,int count);
}
