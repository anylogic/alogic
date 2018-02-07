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
	public Principal getCurrent(HttpServletRequest request,HttpServletResponse response) {
		Session sess = sessionManager.getSession(request,response, true);
		return getCurrent(request,response,sess);
	}

	@Override
	public Principal getCurrent(HttpServletRequest request,HttpServletResponse response,Session session) {
		Session sess = session;
		if (sess == null){
			//保证session不为空
			sess = sessionManager.getSession(request,response, true);
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
		if (StringUtils.isNotEmpty(token)) {
			try {
				Parameters paras = theCall.createParameter();

				paras.param("token", token);
				paras.param("fromIp", getClientIp(request));
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
						LOG.error(String.format("Token %s has not logged in.",token));
					}
				}else{
					LOG.error(String.format("Remote call failed,Token %s has not logged in.",token));
				}

			} catch (Exception ex) {
				LOG.error("Remote call failed.");
				LOG.error(ExceptionUtils.getStackTrace(ex));
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
			Principal principal = new SessionPrincipal(session.getId(),session);
			LOG.info(String.format("User %s has logged out.",principal.getLoginId()));						
			principal.expire();
		}
	}

}
