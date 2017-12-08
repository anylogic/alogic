package com.alogic.auth.sso.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Element;

import com.alogic.auth.AuthenticationHandler;
import com.alogic.auth.Principal;
import com.alogic.auth.Session;
import com.alogic.auth.SessionManager;
import com.alogic.auth.SessionPrincipal;
import com.alogic.metrics.Fragment;
import com.alogic.remote.call.BuilderFactory;
import com.alogic.remote.call.Call;
import com.alogic.remote.call.Parameters;
import com.alogic.remote.call.Result;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * SSO客户端的验证器
 * @author yyduan
 *
 */
public class ClientSideHandler extends AuthenticationHandler.Abstract{
	/**
	 * 远程调用
	 */
	protected Call theCall = null;
	
	/**
	 * 会话管理器
	 */
	protected SessionManager sessionManager = null;
	
	/**
	 * 参数token的参数id
	 */
	protected String arguToken = "token";
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		
		Element callElem = XmlTools.getFirstElementByPath(e, "call");
		if (callElem != null){
			Factory<Call> factory = new Factory<Call>();
			try {
				theCall = factory.newInstance(callElem, props, "module");
			}catch (Exception ex){
				LOG.error(String.format("Can not create call instance by %s", 
						XmlTools.node2String(callElem)));
				LOG.error(ExceptionUtils.getStackTrace(ex));
			}
		}
		
		configure(props);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		arguToken = PropertiesConstants.getString(p,"auth.para.token",arguToken);
	}
	
	@Override
	public Principal getCurrent(HttpServletRequest request) {
		Session sess = sessionManager.getSession(request, false);
		return getCurrent(request,sess);
	}

	@Override
	public Principal getCurrent(HttpServletRequest request,Session session) {
		Principal principal = null;
		
		if (session != null && session.isLoggedIn()){
			//已经登录，并缓存在了本地session中
			principal = new SessionPrincipal(session);
		}else{
			//先从Session中找token
			String token = session.hGet(Session.DEFAULT_GROUP, Session.TOKEN, "");
			if (StringUtils.isEmpty(token)){
				token = request.getParameter(arguToken);
			}
			if (StringUtils.isNotEmpty(token)){
				try {
					Parameters paras = theCall.createParameter();
					
					paras.param("token", token);
					paras.param("fromIp", this.getClientIp(request));
					
					Result result = theCall.execute(paras);
					
					
				}catch (Exception ex){
					LOG.error("Remote call failed.");
					LOG.error(ExceptionUtils.getStackTrace(ex));
				}
			}
		}
		
		return principal;
	}

	@Override
	public boolean hasPrivilege(Principal principal, String privilege) {
		if (principal != null){
			SessionPrincipal thePrincipal = (SessionPrincipal)principal;
			return thePrincipal.hasPrivilege(privilege);
		}
		return false;
	}

	@Override
	public Principal getPrincipal(String app, String token) {
		throw new BaseException("core.e1000","In default mode,it's not supported to get principal by token.");
	}

	@Override
	public Principal login(HttpServletRequest request) {
		throw new BaseException("core.e1000","In sso client mode,it's not supported to login.");
	}
	
	@Override
	public void logout(Principal principal) {
		throw new BaseException("core.e1000","In sso client mode,it's not supported to login.");
	}

	@Override
	public void setSessionManager(SessionManager sm) {
		this.sessionManager = sm;
	}

}
