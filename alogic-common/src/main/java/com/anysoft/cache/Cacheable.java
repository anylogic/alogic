package com.anysoft.cache;

import com.anysoft.util.JsonSerializer;
import com.anysoft.util.XmlSerializer;


/**
 * 可缓存
 * @author duanyy
 * @since 1.0.6
 * 
 * @version 1.3.0 [20140727 duanyy] <br>
 * - 修正类名为Cacheable <br>
 * 
 * @version 1.6.3.2 <br> 
 * - 增加方法{@link #expire()},对象可在该方法实现中释放资源<br>
 * 
 * @deprecated
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
	 * @return true if expired, or not return false
	 */
	public boolean isExpired();	
	
	/**
	 * 将该对象失效
	 */
	public void expire();
}
