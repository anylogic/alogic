package com.logicbus.redis.util;

public class RedisContextException extends RedisException {

	private static final long serialVersionUID = 3757846660256615807L;

	public RedisContextException(String _code, String _message,
			Exception _source) {
		super( _code, _message, _source);
	}

	public RedisContextException(String _code, String _message) {
		super( _code, _message);
	}

}
