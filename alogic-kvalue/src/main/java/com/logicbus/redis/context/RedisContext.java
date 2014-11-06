package com.logicbus.redis.context;

/**
 * RedisContext
 * 
 * @author duanyy
 * 
 * @since 1.0.0.1
 * 
 */
public interface RedisContext extends AutoCloseable{
	public RedisPool getPool(String id);
}
