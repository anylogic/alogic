package com.logicbus.remote.client;

/**
 * 请求参数
 * 
 * @author duanyy
 * @since 1.2.3
 * 
 * @version 1.2.4.2 [20140709 duanyy]
 * - 增加encoding方法来指定编码
 */
public interface Parameter {
	
	/**
	 * 设置参数
	 * @param id 参数ID 
	 * @param value 参数值
	 * @return
	 */
	public Parameter param(String id,String value);
	
	/**
	 * 设置参数列表
	 * @param _params 参数列表
	 * @return
	 */
	public Parameter params(String ... _params);
	
	/**
	 * 设置编码
	 * @param _encoding
	 * @return
	 * 
	 * @since 1.2.4.2
	 */
	public Parameter encoding(String _encoding);
	
	/**
	 * 组合成字符串
	 * @return
	 */
	public String toString();
}
