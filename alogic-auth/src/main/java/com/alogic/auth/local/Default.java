package com.alogic.auth.local;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.alogic.auth.AuthenticationHandler;
import com.alogic.auth.Constants;
import com.alogic.auth.CookieManager;
import com.alogic.auth.Principal;
import com.alogic.auth.Session;
import com.alogic.auth.SessionManager;
import com.alogic.auth.SessionPrincipal;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Pair;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.server.http.HttpContext;

/**
 * 新版缺省实现
 * 
 * @author yyduan
 * @since 1.6.11.59 [20180911 duanyy]
 */
public class Default extends AuthenticationHandler.Abstract{
	
	/**
	 * 会话管理器
	 */
	protected SessionManager sessionManager = null;
	
	/**
	 * 当Principal输出时的脚本
	 */
	protected Logiclet onPrincipal = null;
	
	/**
	 * 当登录时执行脚本
	 */
	protected Logiclet onLogin = null;
	
	/**
	 * 当注销时执行脚本
	 */
	protected Logiclet onLogout = null;
	
	/**
	 * 扩展指令时执行脚本
	 */
	protected Logiclet onCommand = null;
	
	/**
	 * 验证菜单权限的脚本
	 */
	protected Script onMenu = null;
	
	/**
	 * encoding
	 */
	protected String encoding = "utf-8";
	
	protected String dftApp = "";
	
	protected Session getSession(SessionManager sm,HttpServletRequest request,HttpServletResponse response,boolean create){
		return sm.getSession(request,response,create);
	}
	
	@Override
	public boolean isLocalLoginMode(){
		return true;
	}
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		
		Element elem = XmlTools.getFirstElementByPath(e, "on-principal");
		if (elem != null){
			onPrincipal = Script.create(elem, props);
		}
		
		elem = XmlTools.getFirstElementByPath(e, "on-login");
		if (elem != null){
			onLogin = Script.create(elem, props);
		}
		
		elem = XmlTools.getFirstElementByPath(e, "on-logout");
		if (elem != null){
			onLogout = Script.create(elem, props);
		}
		
		elem = XmlTools.getFirstElementByPath(e, "on-command");
		if (elem != null){
			onCommand = Script.create(elem, props);
		}
		
		elem = XmlTools.getFirstElementByPath(e, "on-menu");
		if (elem != null){
			onMenu = Script.create(elem, props);
		}
		
		configure(props);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		dftApp = PropertiesConstants.getString(p,"dftApp",dftApp,true);
		encoding = PropertiesConstants.getString(p,"http.encoding",encoding);
	}
	
	@Override
	public Principal getCurrent(HttpServletRequest request,HttpServletResponse response) {
		Session sess = getSession(sessionManager,request,response, false);
		return getCurrent(request,response,sess);
	}

	@Override
	public Principal getPrincipal(String app,String token,String callback) {
		Session session = this.sessionManager.getSession(token,false);
		return (session != null && session.isLoggedIn()) ? new ThePrincipal(token,session,onPrincipal,app):null;
	}	
	
	@Override
	public Principal getCurrent(HttpServletRequest request,HttpServletResponse response,Session session) {
		return (session != null && session.isLoggedIn()) ? new ThePrincipal(session.getId(),session,onPrincipal,dftApp):null;
	}
	
	@Override
	public Principal login(HttpServletRequest request,HttpServletResponse response) {
		Context ctx = new HttpContext(request,response,encoding);
		return login(ctx);
	}
	
	@Override
	public Principal login(Context ctx) {
		if (onLogin == null){
			throw new BaseException("core.e1000","This function is not supported.");
		}
		
		HttpContext httpContext = (HttpContext)ctx;
		HttpServletRequest request = httpContext.getRequest();
		HttpServletResponse response = httpContext.getResponse();		
		
		Session sess = getSession(sessionManager,request,response, true);		
		LogicletContext logicletContext = new Context.ServantLogicletContext(ctx);	
		CookieManager cm = new CookieManager.Default(this.sessionManager, request, response);
		try {
			Principal principal = new ThePrincipal(sess.getId(),sess,onPrincipal,dftApp);			
			logicletContext.setObject(Constants.ID_PRINCIPAL, principal);
			logicletContext.setObject(Constants.ID_SESSION, sess);		
			logicletContext.setObject(Constants.ID_COOKIES, cm) ;
			XsObject doc = new JsonObject("root",new HashMap<String,Object>());
			onLogin.execute(doc,doc, logicletContext, null);
			return principal;
		}finally{
			logicletContext.removeObject(Constants.ID_PRINCIPAL);
			logicletContext.removeObject(Constants.ID_SESSION);
			logicletContext.removeObject(Constants.ID_COOKIES);
		}			
	}
	
	@Override
	public void logout(HttpServletRequest request,HttpServletResponse response) {
		Context ctx = new HttpContext(request,response,encoding);
		logout(ctx);		
	}
	
	@Override
	public void logout(Context ctx){
		if (onLogin == null){
			throw new BaseException("core.e1000","This function is not supported.");
		}
		
		HttpContext httpContext = (HttpContext)ctx;
		HttpServletRequest request = httpContext.getRequest();
		HttpServletResponse response = httpContext.getResponse();		
		
		CookieManager cm = new CookieManager.Default(this.sessionManager, request, response);
		Session sess = getSession(sessionManager,request,response, true);		
		LogicletContext logicletContext = new Context.ServantLogicletContext(ctx);				
		try {
			Principal principal = new ThePrincipal(sess.getId(),sess,onPrincipal,dftApp);			
			logicletContext.setObject(Constants.ID_PRINCIPAL, principal);
			logicletContext.setObject(Constants.ID_SESSION, sess);
			logicletContext.setObject(Constants.ID_COOKIES, cm) ;
			XsObject doc = new JsonObject("root",new HashMap<String,Object>());
			onLogout.execute(doc,doc, logicletContext, null);
		}finally{
			logicletContext.removeObject(Constants.ID_PRINCIPAL);
			logicletContext.removeObject(Constants.ID_SESSION);
			logicletContext.removeObject(Constants.ID_COOKIES);
		}			
	}	
	
	@Override
	public void command(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		Map<String,Object> data = new HashMap<String,Object>();
		if (onCommand != null){
			HttpContext httpContext = (HttpContext)ctx;
			HttpServletRequest request = httpContext.getRequest();
			HttpServletResponse response = httpContext.getResponse();		
			
			CookieManager cm = new CookieManager.Default(this.sessionManager, request, response);
			Session sess = getSession(sessionManager,request,response, true);		
			LogicletContext logicletContext = new Context.ServantLogicletContext(ctx);				
			try {
				Principal principal = new ThePrincipal(sess.getId(),sess,onPrincipal,dftApp);			
				logicletContext.setObject(Constants.ID_PRINCIPAL, principal);
				logicletContext.setObject(Constants.ID_SESSION, sess);
				logicletContext.setObject(Constants.ID_COOKIES, cm) ;
				XsObject doc = new JsonObject("root",data);
				onCommand.execute(doc,doc, logicletContext, null);
			}finally{
				logicletContext.removeObject(Constants.ID_PRINCIPAL);
				logicletContext.removeObject(Constants.ID_SESSION);
				logicletContext.removeObject(Constants.ID_COOKIES);
			}				
		}
		msg.getRoot().put("data", data);
	}	
	
	@Override
	public void setSessionManager(SessionManager sm){
		this.sessionManager = sm;
	}
	
	@Override
	public Principal getCurrent(Context ctx) {
		if (!(ctx instanceof HttpContext)){
			throw new BaseException("core.e1002","The Context is not a HttpContext instance.");
		}
		
		HttpContext httpContext = (HttpContext)ctx;
		HttpServletRequest request = httpContext.getRequest();
		HttpServletResponse response = httpContext.getResponse();
		return getCurrent(request,response);
	}
		
	@Override
	public void checkPrivilege(Principal principal,Map<String,Object> menu){
		if (menu != null){
			LogicletContext logicletContext = new LogicletContext(Settings.get());				
			try {			
				logicletContext.setObject(Constants.ID_PRINCIPAL, principal);						
				XsObject doc = new JsonObject("root",menu);
				onMenu.execute(doc,doc, logicletContext, null);
			}finally{
				logicletContext.removeObject(Constants.ID_PRINCIPAL);
			}	
		}
	}
	
	/**
	 * principal实现
	 * @author yyduan
	 *
	 */
	public static class ThePrincipal extends SessionPrincipal{
		/**
		 * a logger of slf4j
		 */
		protected final Logger LOG = LoggerFactory.getLogger(ThePrincipal.class);
		
		/**
		 * 输出时的脚本
		 */
		protected Logiclet onReport = null;
		
		public ThePrincipal(String id, Session session,Logiclet onReport,String app) {
			super(id, session,app);
			this.onReport = onReport;
		}
		
		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				if (onReport == null){
					JsonTools.setString(json, "id", getId());
					
					List<Pair<String,String>> entries = this.session.hGetAll(USER_GROUP,"*");					
					if (entries != null){
						Map<String,Object> map = new HashMap<String,Object>();						
						for (Pair<String,String> p:entries){
							JsonTools.setString(map,p.key(), p.value());
						}						
						json.put("property", map);
					}
					
					List<String> privileges = this.getPrivileges();
					if (privileges != null && !privileges.isEmpty()){
						json.put("privilege", privileges);
					}
				}else{
					LogicletContext logicletContext = new LogicletContext(Settings.get());					
					try {
						logicletContext.setObject(Constants.ID_PRINCIPAL, this);
						XsObject doc = new JsonObject("root",json);
						onReport.execute(doc,doc, logicletContext, null);
					}catch (Exception ex){
						LOG.info("Failed to execute onload script" + ExceptionUtils.getStackTrace(ex));
					}finally{
						logicletContext.removeObject(Constants.ID_PRINCIPAL);
					}					
				}
			}
		}		
	}
}
