package com.logicbus.redis.context;

import com.anysoft.util.Reportable;

/**
 * RedisContext
 * 
 * @author duanyy
 * 
 * @since 1.0.0.1
 * 
 */
public interface RedisContext extends AutoCloseable,Reportable{
	
	/**
	 * 根据ID获取指定的RedisPool
	 * 
	 * @param id 指定的ID
	 * @return RedisPool,如果没有定义则返回为空
	 */
	public RedisPool getPool(String id);
}
