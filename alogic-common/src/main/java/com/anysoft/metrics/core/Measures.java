package com.anysoft.metrics.core;

import com.anysoft.util.JsonSerializer;
import com.anysoft.util.XmlSerializer;


/**
 * 指标的量度集
 * 
 * @author duanyy
 * @since 1.2.8
 */
public interface Measures extends XmlSerializer,JsonSerializer{
	
	public static enum Method {
		lst,
		avg,
		max,
		min,
		sum
	}
	
	/**
	 * 获取量度的叠加方法
	 * @return
	 */
	public Method method();
	
	/**
	 * 设置量度的叠加方法
	 * @param method
	 * @return
	 */
	public Measures method(Method method);
	
	/**
	 * 量度的叠加
	 * @param other
	 * @return
	 */
	public Measures incr(Measures other);
	
	/**
	 * 从左边增加一个或多个量度
	 * @param values
	 * @return
	 */
	public Measures lpush(Object [] values);
	
	/**
	 * 从右边增加一个或多个量度
	 * @param values
	 * @return
	 */
	public Measures rpush(Object [] values);
	
	/**
	 * 获取量度的个数
	 * @return
	 */
	public int count();
	
	/**
	 * 获取指定量度的类型
	 * @param idx
	 * @return
	 */
	public char type(int idx);
	
	/**
	 * 获取所有量度的类型
	 * @return
	 */
	public char [] types();
	
	/**
	 * 获取所有量度值
	 * @return
	 */
	public String [] values();
	
	/**
	 * 获取指定维度的量度值
	 * @param idx
	 * @return
	 */
	public Object get(int idx);
	
	/**
	 * 获取指定的量度
	 * @param idx
	 * @return
	 */
	public Long asLong(int idx);
	
	/**
	 * 获取指定的量度
	 * @param idx
	 * @return
	 */
	public Double asDouble(int idx);
	
	/**
	 * 获取指定的量度
	 * @param idx
	 * @return
	 */
	public String asString(int idx);
}
