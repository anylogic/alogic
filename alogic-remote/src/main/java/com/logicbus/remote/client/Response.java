package com.logicbus.remote.client;

/**
 * 服务器响应
 * @author duanyy
 * @since 1.0.4
 * 
 * @version 1.0.7 [20140418 duanyy]<br>
 * - 增加{@link com.logicbus.remote.client.Response#getResponseAttributeNames() getResponseAttributeNames()}
 * 
 */
public interface Response {

	/**
	 * 设置服务器响应的属性
	 * @param name 属性名
	 * @param value 属性值
	 */
	public void setResponseAttribute(String name,String value);
	
	
	/**
	 * 获取需要响应的属性名列表
	 * @return
	 * 
	 * @since 1.0.7
	 */
	public String [] getResponseAttributeNames();
	
	/**
	 * 获取缓冲区对象
	 * @return StringBuffer
	 */
	public StringBuffer getBuffer();	
	
	/**
	 * 准备Buffer
	 * 
	 * @param flag
	 * @since 1.2.2
	 */
	public void prepareBuffer(boolean flag);	
}
