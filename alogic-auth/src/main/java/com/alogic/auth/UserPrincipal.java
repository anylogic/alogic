package com.alogic.auth;

import java.util.List;

/**
 * 基于用户的Principal
 * 
 * @author yyduan
 * 
 * @since 1.6.10.10
 */
public interface UserPrincipal extends Principal{
	
	/**
	 * 获取用户id
	 * @return 用户id
	 */
	public String getUserId();
	
	/**
	 * 获取用户姓名
	 * @return 用户姓名
	 */
	public String getName();
	
	/**
	 * 获取头像
	 * @return 头像图标
	 */
	public String getAvatar();
	
	/**
	 * 获取用户的权限列表
	 * @return 权限列表
	 */
	public List<String> getPrivileges();
	
	/**
	 * 是否具备指定的权限
	 * @param privilege 权限项
	 * @return　如果具备该权限，返回为true，反之为false
	 */
	public boolean hasPrivilege(String privilege);
}
