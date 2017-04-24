package com.alogic.remote;

import java.io.InputStream;

import com.anysoft.util.Properties;

/**
 * 调用Client
 * @author yyduan
 * @since 1.6.8.12
 */
public interface Request {
	/**
	 * 设置请求header
	 * @param name name
	 * @param value value
	 * @return Request
	 */
	public Request setHeader(String name,String value);

	/**
	 * 设置调用正文
	 * @param text 文本形式的正文
	 * @return Request
	 */
	public Request setBody(String text);
	
	/**
	 * 设置调用正文
	 * @param body byte数组形式的正文
	 * @return Request 
	 */
	public Request setBody(byte[] body);
	
	/**
	 * 设置调用正文
	 * @param in 输入流形式的正文
	 * @return Request
	 */
	public Request setBody(InputStream in);
	
	/**
	 * 执行调用，获取响应
	 */
	public Response execute(String path,String key,Properties ctx);
		
	/**
	 * 释放Client
	 */
	public void release();
}
