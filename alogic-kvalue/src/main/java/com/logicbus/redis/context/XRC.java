package com.logicbus.redis.context;

import com.anysoft.context.XMLResource;


/**
 * 基于外部XML的Context实现
 * 
 * @author duanyy
 *
 * @since 1.0.0.1
 * 
 */
public class XRC extends XMLResource<RedisPool> implements RedisContext{

	
	public String getObjectName() {
		return "rcp";
	}

	
	public String getDefaultClass() {
		return RedisPool.class.getName();
	}

	
	public String getDefaultXrc() {
		return "java:///com/logicbus/kvalue/context/kvalue.context.default.xml#com.logicbus.kvalue.context.XRC";
	}


	public RedisPool getPool(String id) {
		return get(id);
	}

}
