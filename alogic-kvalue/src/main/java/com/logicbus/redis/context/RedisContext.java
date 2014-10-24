package com.logicbus.redis.context;

import com.anysoft.util.Watcher;
import com.anysoft.util.XMLConfigurable;

/**
 * PoolFactory
 * @author duanyy
 *
 */
public interface RedisContext extends XMLConfigurable,AutoCloseable{
	
	/**
	 * 获取指定ID的Pool
	 * @param id
	 * @return
	 */
	public RedisPool getPool(String id);
	
	/**
	 * 注册监控器
	 * @param watcher
	 */
	public void addWatcher(Watcher<RedisPool> watcher);
	
	/**
	 * 注销监控器
	 * @param watcher
	 */
	public void removeWatcher(Watcher<RedisPool> watcher);
}
