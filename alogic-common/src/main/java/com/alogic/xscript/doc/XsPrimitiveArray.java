package com.alogic.xscript.doc;

/**
 * Primitive Array
 * @author yyduan
 * @since 1.6.8.14
 */
public interface XsPrimitiveArray {
	
	/**
	 * 增加元素
	 * @param value 元素数据
	 */
	public void add(Number value);
	
	/**
	 * 增加元素
	 * @param value 元素数据
	 */
	public void add(String value);
	
	/**
	 * 增加元素
	 * @param value 元素数据
	 */
	public void add(Boolean value);	
	
	/**
	 * 获取数组元素的个数
	 * @return 元素的个数
	 */
	public int getElementCount();
	
	/**
	 * 获取指定索引的元素
	 * @param index 索引位置
	 * @return 元素
	 */
	public XsPrimitive get(int index);
}
