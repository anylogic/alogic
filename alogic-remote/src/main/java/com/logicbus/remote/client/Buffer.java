package com.logicbus.remote.client;

import com.anysoft.util.DefaultProperties;

/**
 * 服务调用缓冲区 
 * 
 * <br>
 * 用于保存输入输出数据。
 * 
 * @author duanyy
 * @since 1.0.4
 * 
 * @version 1.0.7 [20140418 duanyy]
 * - Request和Response更新
 * 
 * 
 */
public class Buffer extends DefaultProperties implements Request,Response{
	
	/**
	 * 构造函数
	 * @param bufSize 
	 */
	public Buffer(int bufSize){
		content = new StringBuffer(bufSize);
	}
	
	public Buffer(){
		this(2048);
	}
	
	/**
	 * Content
	 */
	protected StringBuffer content = null;
	
	/**
	 * 获取缓冲区对象
	 * @return StringBuffer
	 */
	public StringBuffer getBuffer(){return content;}

	public String toString(){return content.toString();}
	
	/**
	 * 准备Buffer
	 * 
	 * @param flag
	 * @since 1.2.2
	 */
	public void prepareBuffer(boolean flag){
		
	}

	
	public void setResponseAttribute(String name, String value) {
		_SetValue(name, value);
	}

	
	public String getRequestAttribute(String name,String defaultValue) {
		String value = _GetValue(name);
		if (value == null || value.length() <= 0){
			value = defaultValue;
		}
		return value;
	}

	
	public String[] getResponseAttributeNames() {
		return null;
	}

	
	public String[] getRequestAttributeNames() {
		return null;
	}
}
