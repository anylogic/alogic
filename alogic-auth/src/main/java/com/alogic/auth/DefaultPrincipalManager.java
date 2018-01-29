package com.alogic.auth;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Element;

import com.alogic.auth.local.DefaultAuthenticationHandler;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.Context;

/**
 * PrincipalManager缺省实现
 * 
 * <p>
 * 在本实现中，将SessionManager和AuthenticationHandler功能分别委托给两个插件对象处理
 * 
 * @author duanyy
 * @since 1.6.10.10
 * 
 * @version 1.6.11.1 [20171215 duanyy] <br>
 * - 修正退出登录方法<br>
 * 
 * @version 1.6.11.7 [20180107 duanyy] <br>
 * - 优化Session管理 <br>
 * 
 * @version 1.6.11.14 [duanyy 20180129] <br>
 * - 优化AuthenticationHandler接口 <br>
 */
public class DefaultPrincipalManager extends PrincipalManager.Abstract{
	
	/**
	 * Session Manager
	 */
	protected SessionManager sessionManager = null;
	
	/**
	 * Authentication Handler
	 */
	protected AuthenticationHandler authHandler = null;
	
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		
		Element elem = XmlTools.getFirstElementByPath(e, "handler");
		if (elem != null){
			Factory<AuthenticationHandler> f = new Factory<AuthenticationHandler>();			
			try {
				authHandler = f.newInstance(elem, props, "module");
			}catch (Exception ex){
				LOG.error("Can not create authentication handler:" + XmlTools.node2String(elem));
				LOG.error(ExceptionUtils.getStackTrace(ex));
			}
		}
		
		elem = XmlTools.getFirstElementByPath(e,"session");
		if (elem != null){
			Factory<SessionManager> f = new Factory<SessionManager>();
			try {
				sessionManager = f.newInstance(elem, props, "module",LocalSessionManager.class.getName());
			}catch (Exception ex){
				LOG.error("Can not create session manager:" + XmlTools.node2String(elem));
				LOG.error(ExceptionUtils.getStackTrace(ex));
			}
		}
		
		configure(props);
	}
	
	@Override
	public void configure(Properties p){
		if (sessionManager == null){
			sessionManager = new LocalSessionManager();
			sessionManager.configure(p);
			LOG.info("Current session manager is null.Use default instead:" + sessionManager.getClass().getName());
		}
		
		if (authHandler == null){
			authHandler = new DefaultAuthenticationHandler();
			authHandler.configure(p);
			LOG.info("Current authentication handler is null.Use default instead:" + authHandler.getClass().getName());
		}
		
		//为handler设置会话管理器
		authHandler.setSessionManager(sessionManager);
	}
	
	@Override
	public void setSessionManager(SessionManager sm){
		// 本方法无效，采用内置的SessionManager
	}
	
	@Override
	public Session getSession(HttpServletRequest request,HttpServletResponse reponse,boolean create) {
		return sessionManager.getSession(request,reponse,create);
	}

	@Override
	public Principal getCurrent(HttpServletRequest req,HttpServletResponse res) {
		return authHandler.getCurrent(req,res);
	}

	@Override
	public Principal login(HttpServletRequest req,HttpServletResponse res) {
		return authHandler.login(req,res);
	}

	@Override
	public Principal getCurrent(Context ctx) {
		return authHandler.getCurrent(ctx);
	}

	@Override
	public Principal getCurrent(HttpServletRequest req,HttpServletResponse res,Session session) {
		return authHandler.getCurrent(req,res,session);
	}
	
	@Override
	public Principal getPrincipal(String app,String token,String callback) {
		return authHandler.getPrincipal(app,token,callback);
	}
	
	@Override
	public Principal login(Context ctx) {
		return authHandler.login(ctx);
	}

	@Override
	public boolean hasPrivilege(Principal principal, String privilege) {
		return authHandler.hasPrivilege(principal, privilege);
	}
	
	@Override
	public boolean hasPrivilege(Principal principal,String privilege,String objectId,String objectType){
		return authHandler.hasPrivilege(principal,privilege);
	}
	
	@Override
	public void checkPrivilege(Principal principal,Map<String,Object> menu){
		authHandler.checkPrivilege(principal, menu);
	}
	
	@Override
	public void checkPrivilege(Principal principal,Map<String,Object> menu,String objectId,String objectType){
		authHandler.checkPrivilege(principal, menu, objectId, objectType);
	}

	@Override
	public void logout(Context ctx) {
		authHandler.logout(ctx);
	}

	@Override
	public void logout(HttpServletRequest request,HttpServletResponse response) {
		authHandler.logout(request,response);
	}

	@Override
	public Session getSession(String sessionId,boolean create) {
		return sessionManager.getSession(sessionId,create);
	}

	@Override
	public void delSession(String sessionId) {
		sessionManager.delSession(sessionId);
	}

}
