package com.logicbus.redis.client;

import java.util.List;

import com.logicbus.redis.util.RedisConnectException;

/**
 * 工具集
 * 
 * @author duanyy
 *
 */
public class Toolkit {
	private Connection conn = null;
	
	public Toolkit(Connection _conn){
		conn = _conn;
	}
		
	/**
	 * 向服务器发送指令
	 * 
	 * @param cmd 指令
	 * @param args 参数
	 */
	protected Toolkit sendCommand(final byte[] cmd, final byte[]... args){
		conn().sendCommand(cmd, args);
		return this;
	}
	
	protected Toolkit sendCommand(final byte[] cmd, final String... args){
		conn().sendCommand(cmd, args);
		return this;
	}

	
	/**
	 * 获取应答，应答内容为状态码
	 * @return
	 */
	protected String getStatusCodeReply(){
		return conn().getStatusCodeReply();
	}
	
	/**
	 * 获取应答，应答内容为大块字符串
	 * @return
	 */
	protected String getBulkReply(){
		return conn().getBulkReply();
	}

	/**
	 * 获取应答，应答内容为大块二进制块
	 * @return
	 */	
	protected byte[] getBinaryBulkReply(){
		return conn().getBinaryBulkReply();
	}
	
	/**
	 * 获取应答，应答内容为数值
	 * @return
	 */
	protected Long getIntegerReply(){
		return conn().getIntegerReply();
	}
	
	/**
	 * 获取应答，应答内容为多个大块字符串
	 * @return
	 */
	protected List<String> getMultiBulkReply(List<String> t){
		return conn().getMultiBulkReply(t);
	}
	
	/**
	 * 获取应答，应答内容为多个大块二进制块
	 * @return
	 */
	protected List<byte[]> getBinaryMultiBulkReply(){
		return conn().getBinaryMultiBulkReply();
	}
	
	/**
	 * 获取应答，应答内容为多个对象
	 * @return
	 */
	protected List<Object> getObjectMultiBulkReply(){
		return conn().getObjectMultiBulkReply();
	}
	
	private Connection conn(){
		if (conn == null){
			throw new RedisConnectException("isnull","the connection instance is null");
		}
		return conn;
	}
}
