package com.logicbus.kvalue.core;


/**
 * 基于Byte[]的Row
 * 
 * @author duanyy
 *
 */
public interface ByteArrayRow extends KeyValueRow{
	/**
	 * 设置byte[]值
	 * @param value
	 */
	public boolean set(final byte [] value);
	
	/**
	 * 设置byte[]值
	 * @param value
	 * @param ttl
	 * @param writeIfExist
	 * @param writeIfNotExist
	 */
	public boolean set(final byte [] value,final long ttl,final boolean writeIfExist,final boolean writeIfNotExist);

	/**
	 * 获取byte[]值
	 * @param dftValue 
	 * @return 当不存在或为空时，返回缺省值
	 */
	public byte [] get(final byte [] dftValue);
}
