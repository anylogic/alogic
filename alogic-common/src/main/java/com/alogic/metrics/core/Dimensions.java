package com.alogic.metrics.core;

import com.anysoft.formula.DataProvider;
import com.anysoft.util.JsonSerializer;

/**
 * 维度集
 * 
 * @author duanyy
 *
 */
public interface Dimensions extends JsonSerializer,DataProvider{
	
	/**
	 * 设置指定的维度及维度值
	 * 
	 * 设置key的维度的值，如果该维度不存在，则创建新的维度；如果该维度已经存在，overwrite为true时，改写该维度；为false，放弃本次修改。
	 * 
	 * @param key 维度的key
	 * @param value 维度值
	 * @param overwrite 是否覆盖
	 * 
	 */
	public Dimensions set(String key,String value,boolean overwrite);
	
	/**
	 * 获取指定维度的值
	 * 
	 * 获取指定key的维度值，如果该维度不存在，则返回dftValue.
	 * 
	 * @param key 维度key
	 * @param dftValue 缺省值
	 * @return 维度值
	 */
	public String get(String key,String dftValue);
	
	/**
	 * 是否存在指定的维度
	 * @param key 维度key
	 * @return 是否存在
	 */
	public boolean exist(String key);
}
