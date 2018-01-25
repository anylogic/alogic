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
 * 
 * @version 1.6.11.7 [20180107 duanyy] <br>
 * - 优化Session管理 <br>
 * 
 * @version 1.6.11.13 [20180125 duanyy] <br>
 * - 不再提供基于Session的Store实现 <br>
 */
public interface Session extends SetObject,HashObject,Constants{
		
	/**
	 * 判断是否登录
	 * 
	 * @return 如果已经登录,返回为true,反之为false
	 */
	public boolean isLoggedIn();
	
	/**
	 * 设置是否登录标志
	 * 
	 * @param loggedIn 当前是否登录
	 */
	public void setLoggedIn(boolean loggedIn);
}
