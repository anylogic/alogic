package com.alogic.remote;

import java.io.InputStream;

/**
 * 调用Client
 * @author yyduan
 *
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
	 * 设置服务路径
	 * @param path 服务路径
	 * @return Request
	 */
	public Request setPath(String path);

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
	public Response execute();
		
	/**
	 * 释放Client
	 */
	public void release();
}
