package com.alogic.auth;

import com.alogic.load.HashObject;
import com.alogic.load.SetObject;

/**
 * Web服务器的会话，用于替代HttpSession
 * 
 * <p>
 * 相对于HttpSession,增加了Hash和Set等数据类型的支持.
 * 
 * @author duanyy
 * @since 1.6.10.10
 */
public interface Session extends SetObject,HashObject{
	
	/**
	 * 按name设置对象到Session
	 * 
	 * <p>
	 * 用于将对象以name设置到Session中，此方法是为了承接HttpSession原有功能
	 * 
	 * @param name name
	 * @param value 待设置的对象
	 */
	public void setAttribute(String name,Object value);
	
	/**
	 * 按name获取存储在Session中的对象，
	 * 
	 * <p>
	 * 用于获取存储在Session中的对象，此方法是为了承接HttpSession原有功能。如果对象不存在，返回为null.
	 * 
	 * @param name name
	 * @return 指定name的对象，如果不存在，返回为null
	 */
	public Object getAttribute(String name);
}
