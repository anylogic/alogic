package com.alogic.cas;

import com.alogic.auth.Constants;

/**
 * Cas相关常量
 * 
 * @author yyduan
 * 
 * @version 1.6.11.61 [20180913 duanyy] <br>
 * - 增加部分常量 <br>
 *
 */
public interface CasConstants extends Constants{
	public static final String ID_CAS_TICKET = "$cas-ticket";
	public static final String ID_CAS_SERVICE = "$cas-service";	
	public static final String ID_CAS_VALIDATE_PATH = "$cas-validate-path";
	public static final String ID_CAS_VALIDATE_ENDPOINT = "$cas-validate-endpoint";
	
	public static final String ARGU_TICKET = "ticket";
	public static final String ARGU_SERVICE = "service";
	public static final String ARGU_RETURNURL = "returnUrl";
	public static final String ARGU_LOGOUT_REQUEST = "logoutRequest";
	public static final String ARGU_FROM = "from";
	
}
