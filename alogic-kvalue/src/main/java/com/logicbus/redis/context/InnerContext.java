package com.logicbus.redis.context;

import com.anysoft.context.Inner;


/**
 * Source内部实现
 * 
 * @author duanyy
 *
 * @since 1.0.0.1
 */
public class InnerContext extends Inner<RedisPool> implements RedisContext{

	
	public String getObjectName() {
		return "rcp";
	}

	
	public String getDefaultClass() {
		return RedisPool.class.getName();
	}


	public RedisPool getPool(String id) {
		return get(id);
	}

}
