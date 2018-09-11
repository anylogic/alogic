package com.alogic.auth;

/**
 * 常量
 * 
 * @author yyduan
 * @version 1.6.11.59 [20180911 duanyy] <br>
 * - 增加脚本上下文对象id <br>
 */
public interface Constants {
	
	/**
	 * Session在上下文对象中的对象id
	 */
	public static final String ID_SESSION = "$sess";
	public static final String ID_SESSION_ID = "$sess-id";	
	public static final String ID_SESSION_IS_LOGIN = "$sess-isLogin";
	public static final String ID_SESSION_IS_EXPIRE = "$sess-isExpire";
	
	/**
	 * Principal在上下文对象中的对象id
	 */
	public static final String ID_PRINCIPAL = "$prcpl";
	public static final String ID_PRINCIPAL_ID = "$prcpl-id";
	public static final String ID_PRINCIPAL_LOGIN_ID = "$prcpl-loginId";
	public static final String ID_PRINCIPAL_LOGIN_IP = "$prcpl-loginIP";
	public static final String ID_PRINCIPAL_LOGIN_TIME = "$prcpl-loginTime";
	public static final String ID_PRINCIPAL_APP = "$prcpl-app";
	
	/**
	 * CookieManager在上下文对象中的对象id
	 */
	public static final String ID_COOKIES = "$cookies";
	
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
