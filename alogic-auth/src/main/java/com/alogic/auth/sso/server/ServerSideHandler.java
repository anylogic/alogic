package com.alogic.auth.sso.server;

import com.alogic.auth.Principal;
import com.alogic.auth.Session;
import com.alogic.auth.SessionPrincipal;
import com.alogic.auth.local.DefaultAuthenticationHandler;

/**
 * SSO服务端的Handler
 * 
 * @author yyduan
 * @version 1.6.11.1 [20171215 duanyy] <br>
 * - 增加获取登录id的方法<br>
 * 
 * @version 1.6.11.7 [20180107 duanyy] <br>
 * - 优化Session管理 <br>
 * 
 * @version 1.6.11.14 [duanyy 20180129] <br>
 * - 优化AuthenticationHandler接口 <br>
 */
public class ServerSideHandler extends DefaultAuthenticationHandler{
	
	@Override
	public Principal getPrincipal(String app,String token,String callback) {
		Session session = this.sessionManager.getSession(token,false);
		return (session != null && session.isLoggedIn()) ? new SessionPrincipal(token,session):null;
	}	
}