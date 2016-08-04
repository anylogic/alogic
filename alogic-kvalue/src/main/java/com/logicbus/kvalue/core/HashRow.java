package com.logicbus.kvalue.core;

import java.util.List;
import java.util.Map;

/**
 * 基于Hash的Row
 * 
 * @author duanyy
 * 
 * @version 1.6.5.40 [20160804 duanyy] <br>
 * - 增加getAll接口 <br>
 */
public interface HashRow extends KeyValueRow{
	
	/**
	 * 获取Hash中所有取值的列表
	 * @return
	 */
	public List<String> values();
	
	/**
	 * 获取Hash中所有key的列表
	 * @return
	 */
	public List<String> keys();
	
	/**
	 * 删除Hash中一个或多个Field
	 * @param fields field的key列表
	 * @return
	 */
	public long del(final String...fields);
	
	/**
	 * 测试Hash中是否存在指定的field
	 * @param field
	 * @return
	 */
	public boolean exists(final String field);
	
	/**
	 * 获取Hash中指定的field
	 * @param field
	 * @return
	 */
	public String get(final String field,final String dftValue);
	/**
	 * 获取Hash中指定的field
	 * @param field
	 * @return
	 */
	public long get(final String field,final long dftValue);
	/**
	 * 获取Hash中指定的field
	 * @param field
	 * @return
	 */
	public double get(final String field,final double dftValue);
	
	/**
	 * 获取Hash的所有数据
	 * @return Map<String,String>形式的数据
	 */
	public Map<String,String> getAll();
	
	/**
	 * 获取Hash的所有数据
	 * @param json 预定义的json，如果为空，将创建一个
	 * @return Map<String,Object>形式的数据
	 */	
	public Map<String,Object> getAll(Map<String,Object> json);
	
	/**
	 * 设置Field
	 * @param field
	 * @param value
	 * @return
	 */
	public boolean set(final String field,final String value);
	
	/**
	 * 设置field
	 * @param field
	 * @param value
	 * @return
	 */
	public boolean set(final String field,final long value);
	
	/**
	 * 设置field
	 * @param field
	 * @param value
	 * @return
	 */
	public boolean set(final String field,final double value);
	
	/**
	 * 设置多个field
	 * @param keyvalues
	 * @return
	 */
	public boolean mset(final String...keyvalues);
	
	/**
	 * 对指定Field的值进行增量加减
	 * @param field
	 * @param increment
	 * @return
	 */
	public long incr(final String field,final long increment);
	
	/**
	 * 对指定Field的值进行增量加减
	 * @param field
	 * @param increment
	 * @return
	 */	
	public double incr(final String field,final double increment);
	
	/**
	 * 获取多个field的取值
	 * @param fields
	 * @return
	 */
	public List<String> mget(final String...fields);
	
	/**
	 * 获取Hash中Field个数
	 * @return
	 */
	public long length();
}
