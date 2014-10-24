package com.anysoft.cache;

import com.anysoft.util.Watcher;


/**
 * 缓存对象的提供者
 * @author duanyy
 * @since 1.0.6
 * 
 * @version 1.3.0 [20140727 duanyy]
 * - Cachable修正类名为Cacheable
 * 
 * @version 1.5.2 [20141017 duanyy]
 * - 淘汰ChangeAware机制，采用更为通用的Watcher
 * 
 */
public interface Provider<data extends Cacheable> {
	
	/**
	 * 装入缓存对象
	 * @param id 对象id
	 * @return
	 * @deprecated
	 */
	public data load(String id);
	
	/**
	 * 装入对象
	 * @param id 对象ID
	 * @param cacheAllowed 允许装入缓存的对象
	 * @return
	 */
	public data load(String id,boolean cacheAllowed);
	
	/**
	 * 注册监控器
	 * @param watcher
	 */
	public void addWatcher(Watcher<data> watcher);
	
	/**
	 * 注销监控器
	 * @param watcher
	 */
	public void removeWatcher(Watcher<data> watcher);
}
