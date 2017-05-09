package com.alogic.xscript.doc;

/**
 * 对象
 * 
 * @author yyduan
 * @since 1.6.8.14
 */
public interface XsObject extends XsElement {
	/**
	 * 获取节点的tag
	 * @return tag
	 */
	public String getTag();
	/**
	 * 增加指定属性
	 * @param name 属性名
	 * @param value 属性值
	 */
	public void addProperty(String name,String value);
	
	/**
	 * 增加指定的属性
	 * @param name 属性名
	 * @param value 属性值
	 */
	public void addProperty(String name,Number value);
	
	/**
	 * 增加指定的属性
	 * @param name 属性名
	 * @param value 属性值
	 */
	public void addProperty(String name,boolean value);
	
	/**
	 * 删除指定的属性或子节点
	 * @param name 属性名
	 */
	public boolean remove(String name);
	
	/**
	 * 当前节点是否具备指定的属性
	 * @param name 属性名
	 * @return 是否具备指定的属性
	 */
	public boolean hasProperty(String name);
	
	/**
	 * 获取指定的属性的属性值
	 * @param name 属性名
	 * @param dft 缺省值
	 * @return 属性值，当该属性不存在时，返回缺省值
	 */
	public String getProperty(String name,String dft);
	
	/**
	 * 获取指定的属性的属性值
	 * @param name 属性名
	 * @param dft 缺省值
	 * @return 属性值，当该属性不存在时，返回缺省值
	 */
	public long getProperty(String name,long dft);
	
	/**
	 * 获取指定的属性的属性值
	 * @param name 属性名
	 * @param dft 缺省值
	 * @return 属性值，当该属性不存在时，返回缺省值
	 */	
	public int getProperty(String name,int dft);
	
	/**
	 * 获取指定的属性的属性值
	 * @param name 属性名
	 * @param dft 缺省值
	 * @return 属性值，当该属性不存在时，返回缺省值
	 */	
	public boolean getProperty(String name,boolean dft);
	
	/**
	 * 获取指定的属性的属性值
	 * @param name 属性名
	 * @param dft 缺省值
	 * @return 属性值，当该属性不存在时，返回缺省值
	 */	
	public float getProperty(String name,float dft);
	
	/**
	 * 获取指定的属性的属性值
	 * @param name 属性名
	 * @param dft 缺省值
	 * @return 属性值，当该属性不存在时，返回缺省值
	 */	
	public double getProperty(String name,double dft);
	
	/**
	 * 获取数组形式的子节点
	 * @param name tag
	 * @param create 如果不存在,则创建一个新的
	 * @return 子节点列表
	 */
	public XsArray getArrayChild(String name,boolean create);
	
	/**
	 * 获取数组形式的子节点
	 * @param name tag
	 * @param create 如果不存在,则创建一个新的
	 * @return 子节点列表
	 */
	public XsPrimitiveArray getPrimitiveArrayChild(String name,boolean create);
	
	/**
	 * 获取子节点
	 * @param name tag
	 * @param create 如果不存在,则创建一个新的
	 * @return 子节点实例
	 */
	public XsObject getObjectChild(String name,boolean create);
}
