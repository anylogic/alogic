package com.logicbus.redis.util;

/**
 * Redis连接过程中的异常
 * 
 * @author duanyy
 *
 */
public class RedisConnectException extends RedisException {


	public RedisConnectException(String _code, String _message) {
		super(_code, _message);
	}
	
	public RedisConnectException(String _code, String _message, Exception _source) {
		super( _code, _message, _source);
	}
	
	private static final long serialVersionUID = -6843884239106050218L;

}
