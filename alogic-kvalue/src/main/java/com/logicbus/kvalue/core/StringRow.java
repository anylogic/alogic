package com.logicbus.kvalue.core;


/**
 * String类型的Row
 * 
 * @author duanyy
 *
 */
public interface StringRow extends KeyValueRow {

	/**
	 * 设置字符串值 
	 * @param value
	 */
	public boolean set(final String value);
	
	/**
	 * 设置字符串值
     * <br>
     * writeIfExist和writeifNotExist有下列组合：<br>
     * - (true,true) 只有该Row存在时才写入，如果Row不存在，操作不成功，但不会报错 <br>
     * - (true,false) 同(true,true) <br>
     * - (false,true) 只有当该Row不存在时才写入，如果Row存在，操作不成功，但不会报错 <br>
     * - (false,false) 无论该Row是否存在，都写入 <br>
     * 
	 * @param value
	 * @param ttl time to live
	 * @param writeIfExist 仅仅Row存在才写入
	 * @param writeifNotExist 仅仅Row不存在才写入
	 */
	public boolean set(final String value,final long ttl,final boolean writeIfExist,final boolean writeifNotExist);
	
	/**
	 * 获取字符串值
	 * @param dftValue 缺省值
	 * @return 字符串值，当不存在或为空时返回缺省值
	 */
	public String get(final String dftValue);
	
	/**
	 * 在指定的位置覆盖字符串
	 * @param offset
	 * @param value
	 * @return 修改之后的字符串长度
	 */
	public long setRange(final long offset,final String value);
	
	/**
	 * 在当前值后面append指定的value
	 * @param value
	 */
	public long append(final String value);
	
	/**
	 * 获取字符串长度
	 * @return
	 */
	public long strlen();
}
