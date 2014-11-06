package com.logicbus.kvalue.context;

import com.anysoft.context.XMLResource;
import com.logicbus.kvalue.core.Schema;
import com.logicbus.redis.kvalue.RedisSchema;

/**
 * 基于外部XML文件的Context实现
 * 
 * @author duanyy
 *
 * @since 1.0.0.1
 * 
 */
public class XRC extends XMLResource<Schema> {

	
	public String getObjectName() {
		return "schema";
	}

	
	public String getDefaultClass() {
		return RedisSchema.class.getName();
	}

	
	public String getDefaultXrc() {
		return "java:///com/logicbus/kvalue/context/kvalue.context.default.xml#com.logicbus.kvalue.context.XRC";
	}

}
