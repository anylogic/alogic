package com.alogic.cas.client;

import java.net.URLEncoder;
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
import com.alogic.remote.util.HttpQuery;
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
 * CasServer的缺省实现
 * 
 * @author yyduan
 * @since 1.6.11.60 [20180912 duanyy] 
 * 
 * @version 1.6.11.61 [20180913 duanyy] <br>
 * - 增加$service和$clientIp等内置变量 <br>
 */
public class DefaultCasServer implements CasServer {	
	/**
	 * a logger of slf4j
	 */
	protected final static Logger LOG = LoggerFactory.getLogger(CasServer.class);

	/**
	 * Server id
	 */
	protected String id;
	
	/**
	 * CAS服务器的登录地址
	 */
	protected String loginURL = "${cas.server}/login";
	
	/**
	 * CAS服务器的token验证地址
	 */
	protected String validateURL = "${cas.server}/serviceValidate";
	
	/**
	 * CAS服务器的登出地址
	 */
	protected String logoutURL = "${cas.server}/logout";
	
	/**
	 * CAS服务器的回调路径
	 */
	protected String callbackPath = "/casclient/cas/${id}";
		
	/**
	 * 回调服务器地址
	 */
	protected String callbackServer = "";
	
	/**
	 * ticket参数名
	 */
	protected String arguTicket = "ticket";
	
	/**
	 * service参数名
	 */
	protected String arguService = "service";
	
	/**
	 * 编码
	 */
	protected String encoding = "utf-8";
	
	/**
	 * 当验证token时执行脚本
	 */
	protected Logiclet onValidate = null;
	
	/**
	 * 当注销时执行脚本
	 */
	protected Logiclet onLogout = null;	
	
	/**
	 * 时间戳
	 */
	private long timestamp = System.currentTimeMillis();
	
	protected String sessionGroup = "$cas-client";	
	
	/**
	 * 支持ForwardedHeader
	 */
	protected String ForwardedHeader = "X-Forwarded-For";
	
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
		loginURL = PropertiesConstants.getString(props, "cas.server.login", loginURL);
		validateURL = PropertiesConstants.getString(props, "cas.server.validate", validateURL);
		logoutURL = PropertiesConstants.getString(props, "cas.server.logout", logoutURL);
		callbackPath = PropertiesConstants.getString(props, "cas.callback.path", callbackPath);
		callbackServer = PropertiesConstants.getString(props, "cas.calback.server", callbackServer);
		arguTicket = PropertiesConstants.getString(props, "cas.para.ticket", arguTicket);
		arguService = PropertiesConstants.getString(props, "cas.para.service", arguService);
		encoding = PropertiesConstants.getString(props,"http.encoding",encoding);
		sessionGroup = PropertiesConstants.getString(props, "cas.client.group",sessionGroup);	
		ForwardedHeader = PropertiesConstants.getString(props,"http.forwardedheader", ForwardedHeader);
		
		if (onValidate == null){
			String script = PropertiesConstants.getString(props, "onValidate","");
			if (StringUtils.isNotEmpty(script)){
				onValidate = Script.createFromContent(script, props);
			}
		}
		
		if (onLogout == null){
			String script = PropertiesConstants.getString(props, "onLogout","");
			if (StringUtils.isNotEmpty(script)){
				onLogout = Script.createFromContent(script, props);
			}
		}		
	}

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		
		Element elem = XmlTools.getFirstElementByPath(e, "on-validate");
		if (elem != null){
			onValidate = Script.create(elem, props);
		}
		
		elem = XmlTools.getFirstElementByPath(e, "on-logout");
		if (elem != null){
			onLogout = Script.create(elem, props);
		}
		
		configure(props);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isOk() {
		return StringUtils.isNotEmpty(id);
	}

	@Override
	public void doLogoutCallback(HttpServletRequest httpReq,
			HttpServletResponse httpResp, SessionManager sm, Session session,
			String sessionId) {
		LOG.info("Call back from cas server.." + httpReq.getRemoteAddr());
		if (StringUtils.isNotEmpty(sessionId)){
			LOG.info("Clear session:" + sessionId);
			sm.delSession(sessionId);
		}
	}

	@Override
	public void doValidate(HttpServletRequest httpReq,
			HttpServletResponse httpResp, SessionManager sm, Session session) {
		//由sso服务器进行回调
		String token = this.getParameter(httpReq, arguTicket, "");
		if (StringUtils.isNotEmpty(token)){
			//参数中指定了新的token
			String oldToken = session.hGet(sessionGroup, Session.TOKEN, "");
			if (!token.equals(oldToken)){
				//记录token到当前的session
				session.hSet(sessionGroup, Session.TOKEN, token, true);
			}
		}else{
			token = session.hGet(sessionGroup, Session.TOKEN, "");
		}
		if (StringUtils.isEmpty(token)){
			throw new BaseException("clnt.e2000","Cas ticket is not found.");
		}		
		if (onValidate != null){
			Context ctx = new HttpContext(httpReq,httpResp,encoding);		
			Session sess = getSession(sm,httpReq,httpResp, true);		
			LogicletContext logicletContext = new Context.ServantLogicletContext(ctx);	
			CookieManager cm = new CookieManager.Default(sm, httpReq, httpResp);
			try {
				String service = getCasService(httpReq,httpResp,session);
				//调用远程服务来验证token
				HttpQuery query = new HttpQuery(validateURL);
				query.param(this.arguTicket, token);
				query.param(this.arguService, service);
				String validateEndpoint = query.toString();
				
				logicletContext.setObject(ID_SESSION, sess);		
				logicletContext.setObject(ID_COOKIES, cm) ;
				logicletContext.SetValue(ID_CAS_TICKET, token);
				logicletContext.SetValue(ID_CAS_SERVICE, service);
				logicletContext.SetValue(ID_CAS_VALIDATE_PATH, validateURL);
				logicletContext.SetValue(ID_CAS_VALIDATE_ENDPOINT, validateEndpoint);
				logicletContext.SetValue("$service", "/cas/Login");
				logicletContext.SetValue("$clientIp",getClientIp(httpReq));					

				XsObject doc = new JsonObject("root",new HashMap<String,Object>());
				onValidate.execute(doc,doc, logicletContext, null);
			}finally{
				logicletContext.removeObject(ID_SESSION);
				logicletContext.removeObject(ID_COOKIES);
			}
		}
	}

	@Override
	public void doLogout(HttpServletRequest httpReq,
			HttpServletResponse httpResp, SessionManager sm, Session session) {
		if (onLogout != null){
			Context ctx = new HttpContext(httpReq,httpResp,encoding);		
			Session sess = getSession(sm,httpReq,httpResp, true);		
			LogicletContext logicletContext = new Context.ServantLogicletContext(ctx);	
			CookieManager cm = new CookieManager.Default(sm, httpReq, httpResp);
			try {
				logicletContext.setObject(ID_SESSION, sess);		
				logicletContext.setObject(ID_COOKIES, cm) ;
				logicletContext.SetValue("$service", "/cas/Logout");
				logicletContext.SetValue("$clientIp",getClientIp(httpReq));						
				XsObject doc = new JsonObject("root",new HashMap<String,Object>());
				onLogout.execute(doc,doc, logicletContext, null);
			}finally{
				logicletContext.removeObject(ID_SESSION);
				logicletContext.removeObject(ID_COOKIES);
			}
		}
		
		try {
			//客户端模式，重定向到服务端
			if (StringUtils.isNotEmpty(logoutURL)){
				httpResp.sendRedirect(logoutURL);
			}	
		}
		catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}
	}

	@Override
	public void doLogin(HttpServletRequest httpReq,
			HttpServletResponse httpResp, SessionManager sm, Session session) {
		String callback = getCasService(httpReq,httpResp,session);		
		try {
			String redirect = String.format("%s?%s=%s", loginURL,arguService,URLEncoder.encode(callback,encoding));
			LOG.info("User has not logged in,redirect to: " + redirect);
			httpResp.sendRedirect(redirect);
		}catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}
	}
	
	protected Session getSession(SessionManager sm,HttpServletRequest request,HttpServletResponse response,boolean create){
		return sm.getSession(request,response,create);
	}
	
	protected String getCasService(HttpServletRequest httpReq,HttpServletResponse httpResp, Session session) {
		StringBuffer callback = new StringBuffer();
		if (StringUtils.isNotEmpty(callbackServer)){
			callback.append(callbackServer);
		}else{
			callback.append(httpReq.getScheme()).append("://")
			.append(httpReq.getServerName()).append(":").append(httpReq.getServerPort()).append(httpReq.getContextPath());
		}		
		callback.append(callbackPath).append("/").append(session.getId());		
		return callback.toString();
	}
	
	/**
	 * 从Request中获取指定的参数
	 * @param request HttpServletRequest
	 * @param id 参数id
	 * @param dftValue 缺省值，当参数不存在时，返回
	 * @return　参数值，如果参数不存在，返回缺省值
	 */
	protected String getParameter(HttpServletRequest request,String id,String dftValue){
		String value = request.getParameter(id);
		return StringUtils.isEmpty(value)?dftValue:value;
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

}
