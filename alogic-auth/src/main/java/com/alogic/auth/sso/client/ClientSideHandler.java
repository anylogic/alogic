package com.alogic.auth.sso.client;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Element;

import com.alogic.auth.AuthenticationHandler;
import com.alogic.auth.Principal;
import com.alogic.auth.Session;
import com.alogic.auth.SessionManager;
import com.alogic.auth.SessionPrincipal;
import com.alogic.remote.call.Call;
import com.alogic.remote.call.Parameters;
import com.alogic.remote.call.Result;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * SSO客户端的验证器
 * @author yyduan
 * @version 1.6.11.1 [20171215 duanyy] <br>
 * - 增加获取登录id的方法<br>
 * 
 * @version 1.6.11.7 [20180107 duanyy] <br>
 * - 优化Session管理 <br>
 * 
 * @version 1.6.11.14 [duanyy 20180129] <br>
 * - 优化AuthenticationHandler接口 <br>
 * 
 * @version 1.6.11.22 [duanyy 20180314] <br>
 * - 增加isLocalLoginMode(是否本地登录模式)的判断 <br>
 * - 增加common(扩展指令接口) <br>
 * 
 * @version 1.6.11.23 [duanyy 20180320] <br>
 * - 修正某些不可配置的参数名 <br>
 * 
 * @version 1.6.11.39 [duanyy 20180628] <br>
 * - getCurrent增加同步锁 <br>
 * 
 * @version 1.6.11.45 [duanyy 20180722] <br>
 * - 增加从cookies获取token的模式 <br>
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
	
	protected String callbackPath = "/logout";
	
	protected String arguCallback = "callback";
	
	protected String tokenCookie = "";
		
	@Override
	public boolean isLocalLoginMode(){
		return false;
	}
	
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
		callbackPath = PropertiesConstants.getString(p,"auth.logout.callback",callbackPath);
		arguCallback = PropertiesConstants.getString(p, "auth.para.callback", arguCallback);
		tokenCookie = PropertiesConstants.getString(p, "tokenFromCookie", tokenCookie);
	}
	
	@Override
	public Principal getCurrent(HttpServletRequest request,HttpServletResponse response) {
		Session sess = sessionManager.getSession(request,response, true);
		return getCurrent(request,response,sess);
	}

	@Override
	public synchronized Principal getCurrent(HttpServletRequest request,HttpServletResponse response,Session session) {
		Session sess = session;
		if (sess == null){
			//保证session不为空
			sess = sessionManager.getSession(request,response, true);
		}
		
		if (sess.isLoggedIn()){
			//已经登录
			String token = sess.hGet(Session.DEFAULT_GROUP, Session.TOKEN, "");
			return new SessionPrincipal(token,sess);
		}
		
		Principal principal = null;
		
		String token = request.getParameter(arguToken);
		if (StringUtils.isNotEmpty(token)){
			//参数中指定了新的token
			String oldToken = sess.hGet(Session.DEFAULT_GROUP, Session.TOKEN, "");
			if (token.equals(oldToken)){
				//和以前的token一致
				if (sess.isLoggedIn()){
					return new SessionPrincipal(token,sess);
				}
			}else{
				//新的token，删除前一个token的用户信息和权限信息
				sess.hDel(Session.USER_GROUP);
				sess.sDel(Session.PRIVILEGE_GROUP);
				//记录token到当前的session
				sess.hSet(Session.DEFAULT_GROUP, Session.TOKEN, token, true);
			}
		}else{
			token = sess.hGet(Session.DEFAULT_GROUP, Session.TOKEN, "");
		}
		
		if (StringUtils.isEmpty(token) && StringUtils.isNotEmpty(tokenCookie)){
			token = sessionManager.getCookie(request, tokenCookie, "");
		}
		
		if (StringUtils.isNotEmpty(token)) {
			try {
				Parameters paras = theCall.createParameter();
				
				paras.param(arguToken, token);
				paras.param("fromIp", getClientIp(request));
				
				String callback = getCallbackURL(request,sess.getId());
				if (StringUtils.isNotEmpty(callback)){
					paras.param(arguCallback, callback);
				}
				
				Result result = theCall.execute(paras);
				if (result.getCode().equals("core.ok")) {
					Map<String, Object> data = result.getData("data");
					boolean isLoggedIn = JsonTools.getBoolean(data,
							"isLoggedIn", false);
					if (isLoggedIn) {
						principal = new SessionPrincipal(token,sess);
						principal.fromJson(data);
						sess.setLoggedIn(isLoggedIn);			
						sess.hSet(Session.DEFAULT_GROUP, Session.TOKEN, token,true);
					}else{
						sess.setLoggedIn(isLoggedIn);
						LOG.error(String.format("Token %s has not logged in.",token));
					}
				}else{
					throw new BaseException("core.e1606","Rpc call failed,can not get token from the server.");
				}

			} catch (Exception ex) {
				throw new BaseException("core.e1606","Rpc call failed,can not get token from the server.");
			}
		}
		return principal;
	}
	
	protected String getCallbackURL(HttpServletRequest request,String callbackId){
		StringBuffer callbackURL = new StringBuffer();
		callbackURL.append(request.getScheme()).append("://")
		.append(request.getServerName()).append(":").append(request.getServerPort()).append(request.getContextPath())
		.append(callbackPath).append("?callback=" + callbackId);
		return callbackURL.toString();
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
	public Principal getPrincipal(String app, String token,String callback) {
		throw new BaseException("core.e1000","In sso client mode,it's not supported to get principal by token.");
	}

	@Override
	public Principal login(HttpServletRequest request,HttpServletResponse response) {
		throw new BaseException("core.e1000","In sso client mode,it's not supported to login.");
	}

	@Override
	public void setSessionManager(SessionManager sm) {
		this.sessionManager = sm;
	}

	@Override
	public void logout(HttpServletRequest request,HttpServletResponse response) {
		Session session = sessionManager.getSession(request,response, false);

		if (session != null && session.isLoggedIn()){
			session.hDel(Session.USER_GROUP);
			session.sDel(Session.PRIVILEGE_GROUP);
			session.setLoggedIn(false);
			Principal principal = new SessionPrincipal(session.getId(),session);
			LOG.info(String.format("User %s has logged out.",principal.getLoginId()));						
			principal.expire();
		}
	}

}
