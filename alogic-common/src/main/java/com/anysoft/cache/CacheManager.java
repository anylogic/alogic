package com.anysoft.cache;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.anysoft.util.Manager;
import com.anysoft.util.Watcher;
import com.anysoft.util.WatcherHub;


/**
 * 对象缓存管理器
 * 
 * @author duanyy
 * @since 1.0.6
 * @param <data> 缓存对象类
 * 
 * @version 1.0.7 [20140409 duanyy]
 * + 增加{@link com.anysoft.cache.CacheManager#_get(String)
 * + 增加{@link com.anysoft.cache.CacheManager#_add(String, Cacheable)
 * 
 * @version 1.3.0 [20140727 duanyy]
 * - Cachable修正类名为Cacheable
 * - 监听器列表采用ChangeAwareHub进行实现
 * 
 * @version 1.3.2 [20140814 duanyy]
 * - 优化get方法的共享锁机制
 * 
 * @version 1.5.2 [20141017 duanyy]
 * - 淘汰ChangeAware机制，采用更为通用的Watcher
 * 
 */
public class CacheManager<data extends Cacheable> extends Manager<data> 
implements Provider<data>,Watcher<data> {

	/**
	 * a logger of log4j
	 */
	protected static Logger logger = LogManager.getLogger(CacheManager.class);
	/**
	 * 委托的Provider
	 */
	protected Provider<data> provider = null;
	
	/**
	 * 锁对象
	 */
	protected Object lock = new Object();
	
	/**
	 * Constructor
	 */
	public CacheManager(){
		
	}
	
	/**
	 * Constructor
	 * @param _provider 委托的Provider
	 */
	public CacheManager(Provider<data> _provider){
		provider = _provider;
		if (provider != null){
			provider.addWatcher(this);
		}
	}
	
	
	public data load(String id) {
		return load(id,true);
	}
	
	
	public data load(String id, boolean noCache) {
		if (provider != null){
			return provider.load(id,noCache);
		}
		return null;
	}	

	
	public data get(String id) {
		data found = super.get(id);
		if (found == null){
			synchronized(lock){
				found = super.get(id);
				if (found == null){
					found = load(id);
					if (found != null){
						super.add(id,found);
					}
				}
			}
		}else{
			if (found.isExpired()){
				//对象已过期
				found = load(id);
				if (found != null){
					add(found);
				}					
			}
		}
		return found;
	}	

	/**
	 * 获取指定ID的数据
	 * 
	 * <br>
	 * 提供一个快速接口给子类使用.
	 * @param id ID
	 * @return
	 * @since 1.0.7
	 */
	protected data _get(String id){
		return super.get(id);
	}
	
	/**
	 * 向容器中增加数据
	 * 
	 * <br>
	 * 提供一个快速接口给子类使用.
	 * @param id id
	 * @param obj 数据对象
	 * @return
	 * @since 1.0.7
	 */
	protected void _add(String id,data obj){
		synchronized (lock){
			super.add(id, obj);
		}
	}
	
	
	public void add(String id, data obj) {
		synchronized (lock){
			super.add(id, obj);
		}
	}	

	
	public void remove(String id) {
		synchronized (lock){
			super.remove(id);
		}
	}
	
	/**
	 * 向缓存中增加对象
	 * @param obj
	 */
	public void add(data obj){
		synchronized (lock){
			super.add(obj.getId(), obj);
		}
	}

	
	public void changed(String id, data _data) {
		synchronized (lock){
			logger.info("model is changed,id = " + id);
			add(id, _data);
		}
		
		if (watchers != null){
			watchers.changed(id, _data);
		}
	}

	
	public void added(String id, data _data) {
		// do nothing
		if (watchers != null){
			watchers.added(id, _data);
		}
	}

	
	public void removed(String id, data _data) {
		synchronized (lock){
			logger.info("model is removed,id = " + id);
			remove(id);
		}
		
		if (watchers != null){
			watchers.changed(id, _data);
		}
	}

	
	public void addWatcher(Watcher<data> watcher) {
		if (watchers != null)
			watchers.addWatcher(watcher);
	}

	
	public void removeWatcher(Watcher<data> watcher) {
		if (watchers != null)
			watchers.removeWatcher(watcher);
	}
	
	protected WatcherHub<data> watchers = new WatcherHub<data>();
}
