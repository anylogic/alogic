package com.logicbus.redis.util;

/**
 * Redis数据异常
 * 
 * @author duanyy
 *
 */
public class RedisDataException extends RedisException {

	public RedisDataException(String _code, String _message, Exception _source) {
		super("data." + _code, _message, _source);
	}

	public RedisDataException(String _code, String _message) {
		super("data." + _code, _message);
	}

	private static final long serialVersionUID = -7996052205007869643L;

}
