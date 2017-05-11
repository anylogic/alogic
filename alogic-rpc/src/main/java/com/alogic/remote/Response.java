package com.alogic.remote;

import java.io.IOException;
import java.io.InputStream;

/**
 * 请求的响应
 * @author yyduan
 * @since 1.6.8.12
 * 
 * @version 1.6.8.15 [20170511 duanyy] <br>
 * - 增加绝对路径调用功能 <br>
 */
public interface Response extends AutoCloseable{
	/**
	 * 获取响应头
	 * @param name name
	 * @param dft 缺省值
	 * @return value
	 */
	public String getHeader(String name,String dft);
	
	/**
	 * 获取content-type
	 * @return content-type
	 */
	public String getContentType();
	
	/**
	 * 获取content-length
	 * @return content-length
	 */
	public int getContentLength();
	
	/**
	 * 获取结果代码
	 * @return 结果代码
	 */
	public int getStatusCode();
	
	/**
	 * 获得结果描述
	 * @return 结果描述
	 */
	public String getReasonPhrase();
	
	/**
	 * 响应正文作为String
	 * @return 响应正文
	 */
	public String asString() throws IOException;
	
	/**
	 * 响应正文作为byte数组
	 * @return 响应正文
	 */
	public byte[] asBytes() throws IOException;
	
	/**
	 * 响应正文作为输入流
	 * @return 响应正文
	 */
	public InputStream asStream() throws IOException;
	
	/**
	 * 抛弃响应结果
	 */
	public void discard();
}
