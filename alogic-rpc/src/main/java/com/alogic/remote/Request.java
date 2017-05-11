package com.alogic.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.anysoft.util.Properties;

/**
 * 调用Client
 * @author yyduan
 * @since 1.6.8.12
 * 
 * @version 1.6.8.15 [20170511 duanyy] <br>
 * - 增加绝对路径调用功能 <br>
 */
public interface Request extends AutoCloseable{
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
	 * 设置直接输出
	 * @param out 输出接口
	 * @return Request
	 */
	public Request setBody(DirectOutput out);
	
	/**
	 * 执行调用，获取响应
	 */
	public Response execute(String path,String key,Properties ctx);
	
	/**
	 * 执行调用，获取响应
	 */
	public Response execute(String fullPath);
		
	/**
	 * 输出流直接输出
	 * @author yyduan
	 *
	 */
	public static interface DirectOutput {
		/**
		 * 输出到OutputStream
		 * @param outstream output stream
		 * @throws IOException
		 */
		public void writeTo(OutputStream outstream) throws IOException;
	}
}
