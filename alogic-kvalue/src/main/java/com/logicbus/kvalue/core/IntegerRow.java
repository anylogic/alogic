package com.logicbus.kvalue.core;


/**
 * 基于Integer的Row
 * 
 * @author duanyy
 *
 */
public interface IntegerRow extends KeyValueRow {
	
	/**
	 * 设置long值
	 * @param value
	 */
	public boolean set(final long value);
	
	/**
	 * 设置long值
	 * @param value
	 * @param ttl
	 * @param writeIfExist
	 * @param writeIfNotExist
	 */
	public boolean set(final long value,final long ttl,final boolean writeIfExist,final boolean writeIfNotExist);

	/**
	 * 获取long值
	 * @param dftValue 缺省值
	 * @return Long值，当不存在，为空或转换错误时，返回缺省值
	 */
	public long get(final long dftValue);
	
	/**
	 * 累加或累减
	 * @param increment
	 */
	public long incr(final long increment);
}
