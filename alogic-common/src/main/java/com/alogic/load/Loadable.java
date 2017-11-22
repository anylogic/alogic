package com.alogic.load;

import com.anysoft.util.Reportable;

/**
 * 可装入接口
 * 
 * @author duanyy
 *
 */
public interface Loadable extends Reportable{
	/**
	 * 获取缓存对象的ID
	 * 
	 * @return ID
	 */
	public String getId();
	
	/**
	 * 获取对象的时间戳
	 * @return 时间戳
	 */
	public long getTimestamp();
	
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
