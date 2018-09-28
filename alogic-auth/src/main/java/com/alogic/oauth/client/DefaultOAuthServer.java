package com.alogic.oauth.client;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.alogic.auth.CookieManager;
import com.alogic.auth.Session;
import com.alogic.auth.SessionManager;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.Context;
import com.logicbus.backend.server.http.HttpContext;

/**
 * 缺省的OAuth2.0服务器实现
 * 
 * @author yyduan
 * @since 1.6.11.61
 */
public class DefaultOAuthServer implements OAuthServer{
	/**
	 * a logger of slf4j
	 */
	protected final static Logger LOG = LoggerFactory.getLogger(OAuthServer.class);	
	
	/**
	 * OAuth2.0服务器的id
	 */
	protected String id;
	
	/**
	 * 当发起登录授权时所调用的脚本
	 */
	protected Logiclet onLoginRequest = null;
	
	/**
	 * 当发起登录授权之后回调所调用的脚本
	 */
	protected Logiclet onLoginCallback = null;
	
	/**
	 * 当发起帐号绑定授权时所调用的脚本
	 */
	protected Logiclet onBindRequest = null;
	
	/**
	 * 当发起帐号绑定授权之后回调所调用的脚本
	 */
	protected Logiclet onBindCallback = null;
	
	/**
	 * 服务器端进行授权的URL地址
	 */
	protected String urlAuthorize = "${oauth.server}/oauth/authorize";
	
	/**
	 * 服务器端进行授权的URL地址
	 */
	protected String urlAccessToken = "${oauth.server}/oauth/access_token";	
	
	/**
	 * CAS服务器的回调路径
	 */
	protected String callbackPath = "/oauthclient/callback/${id}";
		
	/**
	 * 回调服务器地址
	 */
	protected String callbackServer = "";	
	
	/**
	 * 时间戳
	 */
	private long timestamp = System.currentTimeMillis();
	
	/**
	 * 编码
	 */
	protected String encoding = "utf-8";	
	
	protected String sessionGroup = "$oauth-client";	
	
	/**
	 * 在oauth服务器上的clientId
	 */
	protected String clientId = "";
	
	/**
	 * 在oauth服务器上的Secrect
	 */
	protected String clientSecret = "";
	
	/**
	 * 支持ForwardedHeader
	 */
	protected String ForwardedHeader = "X-Forwarded-For";
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isOk() {
		return StringUtils.isNotEmpty(id);
	}	
	
	/**
	 * 获取生存时间(毫秒)
	 * @return 生存时间
	 */
	protected long getTTL(){
		return 5 * 60 * 1000L;
	}
	
	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public boolean isExpired() {
		return System.currentTimeMillis() - this.timestamp > getTTL();
	}

	@Override
	public void expire() {
		this.timestamp = System.currentTimeMillis() - getTTL();
	}

	@Override
	public void report(Element xml) {
		if (xml != null){
			XmlTools.setString(xml,"module",getClass().getName());
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json,"module",getClass().getName());
		}
	}		
	
	@Override
	public void configure(Properties props) {
		id = PropertiesConstants.getString(props, "id", "");
		clientId = PropertiesConstants.getString(props, "clientId", clientId);
		clientSecret = PropertiesConstants.getString(props, "clientSecret", clientSecret);
		urlAuthorize = PropertiesConstants.getString(props, "urlAuthorize", urlAuthorize);
		urlAccessToken = PropertiesConstants.getString(props, "urlAccessToken", urlAccessToken);
		callbackPath = PropertiesConstants.getString(props, "callbackPath", callbackPath);
		callbackServer = PropertiesConstants.getString(props, "callbackServer", callbackServer);
		encoding = PropertiesConstants.getString(props,"http.encoding",encoding);
		sessionGroup = PropertiesConstants.getString(props, "oauth.client.group",sessionGroup);	
		ForwardedHeader = PropertiesConstants.getString(props,"http.forwardedheader", ForwardedHeader);
		
		if (onLoginRequest == null){
			String script = PropertiesConstants.getString(props, "on-login-request","");
			if (StringUtils.isNotEmpty(script)){
				onLoginRequest = Script.createFromContent(script, props);
			}
		}
		
		if (onBindRequest == null){
			String script = PropertiesConstants.getString(props, "on-bind-request","");
			if (StringUtils.isNotEmpty(script)){
				onBindRequest = Script.createFromContent(script, props);
			}
		}
		
		if (onLoginCallback == null){
			String script = PropertiesConstants.getString(props, "on-login-callback","");
			if (StringUtils.isNotEmpty(script)){
				onLoginCallback = Script.createFromContent(script, props);
			}
		}	
		
		if (onBindCallback == null){
			String script = PropertiesConstants.getString(props, "on-bind-callback","");
			if (StringUtils.isNotEmpty(script)){
				onBindCallback = Script.createFromContent(script, props);
			}
		}			
	}

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		
		Element elem = XmlTools.getFirstElementByPath(e, "on-login-request");
		if (elem != null){
			onLoginRequest = Script.create(elem, props);
		}
		
		elem = XmlTools.getFirstElementByPath(e, "on-bind-request");
		if (elem != null){
			onBindRequest = Script.create(elem, props);
		}
		
		elem = XmlTools.getFirstElementByPath(e, "on-login-callback");
		if (elem != null){
			onLoginCallback = Script.create(elem, props);
		}	
		
		elem = XmlTools.getFirstElementByPath(e, "on-bind-callback");
		if (elem != null){
			onBindCallback = Script.create(elem, props);
		}			
		
		configure(props);
	}

	protected Session getSession(SessionManager sm,HttpServletRequest request,HttpServletResponse response,boolean create){
		return sm.getSession(request,response,create);
	}
	
	protected String getCallbackPath(HttpServletRequest httpReq,HttpServletResponse httpResp, Session session,String action){
		StringBuffer callback = new StringBuffer();
		if (StringUtils.isNotEmpty(callbackServer)){
			callback.append(callbackServer);
		}else{
			callback.append(httpReq.getScheme()).append("://")
			.append(httpReq.getServerName()).append(":").append(httpReq.getServerPort()).append(httpReq.getContextPath());
		}		
		callback.append(callbackPath).append("/").append(action);		
		return callback.toString();		
	}

	/**
	 * 获取客户端的ip
	 * @param request HttpServletRequest
	 * @return 客户端ip
	 */
	public String getClientIp(HttpServletRequest request) {
		/**
		 * 支持负载均衡器的X-Forwarded-For
		 */
		String ip = request.getHeader(ForwardedHeader);
		if (StringUtils.isNotEmpty(ip)){
			String [] ips = ip.split(",");
			if (ips.length > 0){
				return ips[0];
			}else{
				return request.getRemoteHost();
			}
		}else{
			return request.getRemoteHost();
		}
	}

	@Override
	public void doLoginRequest(HttpServletRequest httpReq,
			HttpServletResponse httpResp, SessionManager sm, Session session) {
		if (onLoginRequest != null) {
			Context ctx = new HttpContext(httpReq, httpResp, encoding);
			Session sess = getSession(sm, httpReq, httpResp, true);
			LogicletContext logicletContext = new Context.ServantLogicletContext(
					ctx);
			CookieManager cm = new CookieManager.Default(sm, httpReq, httpResp);
			try {
				logicletContext.setObject(ID_SESSION, sess);
				logicletContext.setObject(ID_COOKIES, cm);
				XsObject doc = new JsonObject("root",
						new HashMap<String, Object>());
				
				String callback = getCallbackPath(httpReq,httpResp,sess,"login");
				
				logicletContext.SetValue("$callbackUrl", callback);
				logicletContext.SetValue("$authorizeUrl", this.urlAuthorize);
				logicletContext.SetValue("$clientId", this.clientId);
				logicletContext.SetValue("$clientSecret", this.clientSecret);
				logicletContext.SetValue("$service", "/oauth/Login");
				logicletContext.SetValue("$clientIp",getClientIp(httpReq));					
				
				onLoginRequest.execute(doc, doc, logicletContext, null);
				
				String redirectUrl = PropertiesConstants.getString(logicletContext,"$authorizeUrl",this.urlAuthorize);
				try {
					//客户端模式，重定向到服务端
					if (StringUtils.isNotEmpty(redirectUrl)){
						httpResp.sendRedirect(redirectUrl);
					}	
				}
				catch (Exception ex){
					LOG.error(ExceptionUtils.getStackTrace(ex));
				}	
			} finally {
				logicletContext.removeObject(ID_SESSION);
				logicletContext.removeObject(ID_COOKIES);
			}
		}else{
			throw new BaseException("core.e1000","Oauth login is not supported now.");
		}
	}

	@Override
	public void doBindRequest(HttpServletRequest httpReq,
			HttpServletResponse httpResp, SessionManager sm, Session session) {
		if (onBindRequest != null) {
			Context ctx = new HttpContext(httpReq, httpResp, encoding);
			Session sess = getSession(sm, httpReq, httpResp, true);
			LogicletContext logicletContext = new Context.ServantLogicletContext(
					ctx);
			CookieManager cm = new CookieManager.Default(sm, httpReq, httpResp);
			try {
				logicletContext.setObject(ID_SESSION, sess);
				logicletContext.setObject(ID_COOKIES, cm);
				XsObject doc = new JsonObject("root",
						new HashMap<String, Object>());
				
				String callback = getCallbackPath(httpReq,httpResp,sess,"bind");
				
				logicletContext.SetValue("$callbackUrl", callback);				
				logicletContext.SetValue("$authorizeUrl", this.urlAuthorize);
				logicletContext.SetValue("$clientId", this.clientId);
				logicletContext.SetValue("$clientSecret", this.clientSecret);		
				logicletContext.SetValue("$service", "/oauth/Bind");
				logicletContext.SetValue("$clientIp",getClientIp(httpReq));					
				
				onBindRequest.execute(doc, doc, logicletContext, null);
				
				String redirectUrl = PropertiesConstants.getString(logicletContext,"$authorizeUrl",this.urlAuthorize);
				try {
					//客户端模式，重定向到服务端
					if (StringUtils.isNotEmpty(redirectUrl)){
						httpResp.sendRedirect(redirectUrl);
					}	
				}
				catch (Exception ex){
					LOG.error(ExceptionUtils.getStackTrace(ex));
				}	
			} finally {
				logicletContext.removeObject(ID_SESSION);
				logicletContext.removeObject(ID_COOKIES);
			}
		}else{
			throw new BaseException("core.e1000","Oauth login is not supported now.");
		}	
	}

	@Override
	public void doLoginCallback(HttpServletRequest httpReq,
			HttpServletResponse httpResp, SessionManager sm, Session session) {
		if (onLoginCallback != null) {
			Context ctx = new HttpContext(httpReq, httpResp, encoding);
			Session sess = getSession(sm, httpReq, httpResp, true);
			LogicletContext logicletContext = new Context.ServantLogicletContext(
					ctx);
			CookieManager cm = new CookieManager.Default(sm, httpReq, httpResp);
			try {
				logicletContext.setObject(ID_SESSION, sess);
				logicletContext.setObject(ID_COOKIES, cm);
							
				logicletContext.SetValue("$accesstokenUrl", this.urlAccessToken);
				logicletContext.SetValue("$clientId", this.clientId);
				logicletContext.SetValue("$clientSecret", this.clientSecret);			
				logicletContext.SetValue("$service", "/oauth/LoginCallback");
				logicletContext.SetValue("$clientIp",getClientIp(httpReq));					
				
				XsObject doc = new JsonObject("root",
						new HashMap<String, Object>());
				onLoginCallback.execute(doc, doc, logicletContext, null);	
			} finally {
				logicletContext.removeObject(ID_SESSION);
				logicletContext.removeObject(ID_COOKIES);
			}
		}
	}
	
	@Override
	public void doBindCallback(HttpServletRequest httpReq,
			HttpServletResponse httpResp, SessionManager sm, Session session) {
		if (onBindCallback != null) {
			Context ctx = new HttpContext(httpReq, httpResp, encoding);
			Session sess = getSession(sm, httpReq, httpResp, true);
			LogicletContext logicletContext = new Context.ServantLogicletContext(
					ctx);
			CookieManager cm = new CookieManager.Default(sm, httpReq, httpResp);
			try {
				logicletContext.setObject(ID_SESSION, sess);
				logicletContext.setObject(ID_COOKIES, cm);
							
				logicletContext.SetValue("$accesstokenUrl", this.urlAccessToken);
				logicletContext.SetValue("$clientId", this.clientId);
				logicletContext.SetValue("$clientSecret", this.clientSecret);						
				logicletContext.SetValue("$service", "/oauth/BindCallback");
				logicletContext.SetValue("$clientIp",getClientIp(httpReq));		
				
				XsObject doc = new JsonObject("root",
						new HashMap<String, Object>());
				onBindCallback.execute(doc, doc, logicletContext, null);	
			} finally {
				logicletContext.removeObject(ID_SESSION);
				logicletContext.removeObject(ID_COOKIES);
			}
		}
	}	
}
