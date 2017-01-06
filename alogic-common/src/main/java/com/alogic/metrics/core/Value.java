package com.alogic.metrics.core;

import com.alogic.metrics.core.Fragment.DataType;
import com.alogic.metrics.core.Fragment.Method;
import com.anysoft.util.JsonSerializer;


/**
 * 量度值
 * 
 * @author duanyy
 *
 */
public interface Value extends JsonSerializer{
	
	/**
	 * 累加
	 * @param other 其它的指标
	 * @return 新的实例
	 */
	public Value incr(Value other);
	
	/**
	 * 获取指标的累加方法
	 * @return 累加方法
	 */
	public Method method();
	
	/**
	 * 获取数据类型
	 * @return 数据类型
	 */
	public DataType type();
	
	/**
	 * 获取量度值
	 * @return 量度值
	 */
	public Object value();
	
	/**
	 * 获取字符串值
	 * @param dftValue 缺省值
	 * @return 量度值
	 */
	public String asString(String dftValue);

	/**
	 * 获取double值
	 * @param dftValue 缺省值
	 * @return 量度值
	 */
	public double asDouble(double dftValue);
	
	/**
	 * 获取long值
	 * @param dftValue 缺省值
	 * @return 量度值
	 */
	public long asLong(long dftValue);
	
}
