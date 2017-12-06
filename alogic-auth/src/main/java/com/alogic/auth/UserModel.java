package com.alogic.auth;

/**
 * 用户存储模型
 * 
 * @author yyduan
 * @since 1.6.10.10
 * 
 */
public interface UserModel extends UserPrincipal{
	
	/**
	 * 获取用户密码
	 * @return 用户密码
	 */
	public String getPassword();
}
