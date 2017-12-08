package com.alogic.auth;

/**
 * 常量
 * 
 * @author yyduan
 *
 */
public interface Constants {
	
	/**
	 * 是否登录标记的Key
	 */
	public static final String LOGIN_KEY = "$login";
	
	/**
	 *　验证码
	 */
	public static final String AUTH_CODE = "$login.code";
	
	/**
	 * Token
	 */
	public static final String TOKEN = "$login.token";
	
	/**
	 * 缺省的信息组
	 */
	public static final String DEFAULT_GROUP = "$default";
	
	/**
	 * 用户信息组
	 */
	public static final String USER_GROUP = "$user";
	
	/**
	 * 权限信息组
	 */
	public static final String PRIVILEGE_GROUP = "$privilege";
	
	/**
	 * 用户id
	 */
	public static final String USERID = "userId";
	
	/**
	 * 姓名
	 */
	public static final String NAME = "name";
	
	/**
	 * 头像
	 */
	public static final String AVATAR = "avatar";
	
	/**
	 * 登录时间
	 */
	public static final String LOGIN_TIME = "loginTime";
	
	/**
	 * 客户端ip
	 */
	public static final String FROM_IP = "fromIp";
}
