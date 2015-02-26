package com.alogic.cache.core;

import com.anysoft.cache.Provider;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * 缓存数据集
 * 
 * @author duanyy
 *
 * @param <data>
 * 
 * @since 1.6.3.3
 * 
 */
public interface CacheStore extends Provider<MultiFieldObject>,XMLConfigurable,Reportable {
	
	/**
	 * to get the id of the store
	 * @return id of the store
	 */
	public String id();
	
	public String name();
	
	public String note();
	
	public MultiFieldObject get(String id,boolean cacheAllowed);
	
	public MultiFieldObject expire(String id);
	
	public void expireAll();
}
