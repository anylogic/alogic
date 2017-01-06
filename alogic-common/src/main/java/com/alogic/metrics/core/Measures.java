package com.alogic.metrics.core;

import com.alogic.metrics.core.Fragment.Method;
import com.anysoft.formula.DataProvider;
import com.anysoft.util.JsonSerializer;

/**
 * 量度集
 * 
 * @author duanyy
 *
 */
public interface Measures extends JsonSerializer,DataProvider{
	
	/**
	 * 设置量度值
	 * @param key 量度key
	 * @param value 量度值
	 * @return 量度集实例
	 */
	public Measures set(String key,String value);
	
	/**
	 * 设置量度值
	 * @param key 量度key
	 * @param value 量度值
	 * @param m 汇聚方式
	 * @return 量度集实例
	 */
	public Measures set(String key,long value,Method m);
	
	/**
	 * 设置量度集
	 * @param key 量度key
	 * @param value 量度值
	 * @return 量度集实例
	 */
	public Measures set(String key,long value);
	
	/**
	 * 设置量度值
	 * @param key 量度key
	 * @param value 量度值
	 * @param m 汇聚方式
	 * @return 量度集实例
	 */
	public Measures set(String key,double value,Method m);
	
	/**
	 * 设置量度集
	 * @param key 量度key
	 * @param value 量度值
	 * @return 量度集实例
	 */	
	public Measures set(String key,double value);
	
	/**
	 * 和other汇聚
	 * @param other 其它的量度集
	 * @return 新的量度集
	 */
	public Measures incr(Measures other);
	
	/**
	 * 获取量度值
	 * @param key 量度key
	 * @param dftValue 缺省值
	 * @return 量度值
	 */
	public long getAsLong(String key,long dftValue);
	
	/**
	 * 获取量度值
	 * @param key 量度key
	 * @param dftValue 缺省值
	 * @return 量度值
	 */	
	public String getAsString(String key,String dftValue);
	
	/**
	 * 获取量度值
	 * @param key 量度key
	 * @param dftValue 缺省值
	 * @return 量度值
	 */	
	public double getAsDouble(String key,double value);
	
	/**
	 * 获取量度的汇聚方式
	 * @param key 量度key
	 * @return method
	 */	
	public Method getMethod(String key);
	
	/**
	 * 判断是否存在指定的量度
	 * @param key 量度key
	 * @return 是否存在
	 */
	public boolean exist(String key);
	
}
