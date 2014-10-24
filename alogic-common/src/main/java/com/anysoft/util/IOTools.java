package com.anysoft.util;

import java.io.Closeable;

/**
 * IO的工具类
 * @author duanyy
 * 
 * @version 1.3.4 [20140819 duanyy]
 * - 增加close方法用于关闭多个Closeable或AutoCloseable对象
 * 
 */
public class IOTools {
	/**
	 * 关闭一个或多个输入/输出流
	 * @param closeables 输入输出流列表
	 */
	public static void closeStream(Closeable... closeables) {
		for (Closeable c:closeables){
			if (null != c){
				try{
					c.close();
				}catch (Exception ex){
					
				}
			}
		}
	}
	
	public static void close(AutoCloseable... closeables){
		for (AutoCloseable c:closeables){
			if (null != c){
				try{
					c.close();
				}catch (Exception ex){
					
				}
			}
		}
	}
	
	public static void close(Closeable... closeables) {
		for (Closeable c:closeables){
			if (null != c){
				try{
					c.close();
				}catch (Exception ex){
					
				}
			}
		}
	}	
}
