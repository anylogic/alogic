package com.logicbus.remote.client;

/**
 * 客户端请求
 * @author duanyy
 * @since 1.0.4
 * 
 * @version 1.0.7 [20140418 duanyy]<br>
 * - 增加{@link com.logicbus.remote.client.Request#getRequestAttributeNames() getRequestAttributeNames()}
 * 
 */
public interface Request{
	/**
	 * 获取服务器请求的属性
	 * @param name 属性名
	 * @param defaultValue 缺省值
	 * @return 属性值
	 */
	public String getRequestAttribute(String name,String defaultValue);
	
	/**
	 * 获取需要请求的属性名列表
	 * @return
	 * 
	 * @since 1.0.7
	 */
	public String [] getRequestAttributeNames();
	
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
