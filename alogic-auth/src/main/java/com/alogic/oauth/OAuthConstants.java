package com.alogic.oauth;

import com.alogic.auth.Constants;

/**
 * Oauth2.0相关的常量
 * 
 * @author yyduan
 * @since 1.6.11.61
 * 
 */
public interface OAuthConstants extends Constants{
	/**
	 * 内部跳转
	 */
	public static final String ARGU_RETURNURL = "returnUrl";
	
	/**
	 * client_id
	 */
	public static final String ARGU_CLIENT_ID = "client_id";
	
	/**
	 * response_type
	 */
	public static final String ARGU_RESPONSE_TYPE = "response_type";
	
	/**
	 * redirect_uri
	 */
	public static final String ARGU_REDIRECT_URI = "redirect_uri";
	
	/**
	 * state
	 */
	public static final String ARGU_STATE = "state";
	
	/**
	 * scope
	 */
	public static final String ARGU_SCOPE = "scope";
	
	/**
	 * grant_type
	 */
	public static final String ARGU_GRANT_TYPE = "grant_type";
	
	/**
	 * client_secret
	 */
	public static final String ARGU_CLIENT_SECRET = "client_secret";
	
	/**
	 * access_token
	 */
	public static final String ARGU_ACCESS_TOKEN = "access_token";
	
	/**
	 * refresh_token
	 */
	public static final String ARGU_REFRESH_TOKEN = "refresh_token";
	
	/**
	 * expires_in
	 */
	public static final String ARGU_EXPIRES_IN = "expires_in";
	
	/**
	 * response_type:code
	 */
	public static final String ARGU_RESPONSE_TYPE_CODE = "code";
	
	public static final String ARGU_FROM = "from";
}
