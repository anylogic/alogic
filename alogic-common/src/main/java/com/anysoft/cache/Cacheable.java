package com.anysoft.cache;

import com.anysoft.util.JsonSerializer;
import com.anysoft.util.XmlSerializer;


/**
 * 可缓存
 * @author duanyy
 * @since 1.0.6
 * 
 * @version 1.3.0 [20140727 duanyy]
 * - 修正类名为Cacheable
 * 
 */
public interface Cacheable extends XmlSerializer,JsonSerializer{
	
	/**
	 * 获取缓存对象的ID
	 * 
	 * @return ID
	 */
	public String getId();
	
	/**
	 * 是否已经过期
	 * @return 
	 */
	public boolean isExpired();	
}
