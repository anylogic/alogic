package com.logicbus.kvalue.context;

import com.anysoft.context.Inner;
import com.logicbus.kvalue.core.Schema;
import com.logicbus.redis.kvalue.RedisSchema;

/**
 * Source文件内置Context
 * 
 * @author duanyy
 * 
 * @since 1.0.0.1
 */
public class InnerContext extends Inner<Schema> {

	
	public String getObjectName() {
		return "schema";
	}

	
	public String getDefaultClass() {
		return RedisSchema.class.getName();
	}

}
