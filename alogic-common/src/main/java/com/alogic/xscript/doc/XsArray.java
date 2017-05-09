package com.alogic.xscript.doc;

/**
 * 对象数组
 * @author yyduan
 * 
 * @since 1.6.8.14
 */
public interface XsArray{

	/**
	 * 新创建一个可以增加到数组的元素
	 * @return 新建的元素
	 */
	public XsObject newObject();
	
	/**
	 * 将创建的新元素加入到数组
	 * @param data 新元素
	 */
	public void add(XsObject data);

	/**
	 * 获取数组元素的个数
	 * @return 元素的个数
	 */
	public int getElementCount();
	
	/**
	 * 获取指定位置的元素
	 * @param index 元素的索引位置
	 * @return 元素实例
	 */
	public XsObject get(int index);
}
