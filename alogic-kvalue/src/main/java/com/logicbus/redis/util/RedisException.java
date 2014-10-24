package com.logicbus.redis.util;

import com.anysoft.util.BaseException;

/**
 * Redis异常
 * 
 * @author duanyy
 *
 */
public class RedisException extends BaseException {

	public RedisException(String _code, String _message) {
		super("redis." + _code, _message);
	}

	public RedisException(String _code,String _message,Exception _source){
		super("redis." + _code,_message,_source);
	}
	
	private static final long serialVersionUID = -5684325729233178230L;
}
