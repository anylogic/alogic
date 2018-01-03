package com.alogic.cache.core;

import com.anysoft.cache.Provider;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * 缓存数据集
 * 
 * @author duanyy
 * 
 * @since 1.6.3.3
 * 
 * @version 1.6.4.9 [20151023 duanyy] <br>
 * - 缓存接口增加set方法 <br>
 * 
 * @deprecated
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
	
	/**
	 * 向缓存设置指定ID的数据
	 * @param id 对象id
	 * @param newValue 新的对象
	 * @return 老的对象（如果没有的话，为null)
	 * 
	 * @since 1.6.4.9
	 */
	public MultiFieldObject set(String id,MultiFieldObject newValue);
	
	public void expireAll();
}
