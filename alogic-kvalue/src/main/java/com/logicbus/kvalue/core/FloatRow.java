package com.logicbus.kvalue.core;


/**
 * 基于Float的Row
 * 
 * @author duanyy
 *
 */
public interface FloatRow extends KeyValueRow {
	
	/**
	 * 设置double值
	 * @param value
	 */
	public boolean set(final double value);
	
	/**
	 * 设置 double值
	 * @param value
	 * @param ttl
	 * @param writeIfExist
	 * @param writeIfNotExist
	 */
	public boolean set(final double value,final long ttl,final boolean writeIfExist,final boolean writeIfNotExist);
	
	/**
	 * 获取double值
	 * @param dftValue 缺省值
	 * @return 当不存在或为空时，返回缺省值
	 */
	public double get(final double dftValue);	
	
	/**
	 * 累加或累减
	 * @param increment
	 */
	public double incr(final double increment);
}
