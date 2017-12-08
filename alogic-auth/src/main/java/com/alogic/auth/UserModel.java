package com.alogic.auth;

import com.alogic.load.Loadable;

/**
 * 用户存储模型
 * 
 * @author yyduan
 * @since 1.6.10.10
 * 
 */
public interface UserModel extends Loadable,Constants{
	
	/**
	 * 获取用户密码
	 * @return 用户密码
	 */
	public String getPassword();
	
	/**
	 * 将信息copy到指定的Principal
	 * @param p Principal
	 */
	public void copyTo(Principal p);
}
