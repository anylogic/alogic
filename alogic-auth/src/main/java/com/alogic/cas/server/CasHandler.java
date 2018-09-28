package com.alogic.cas.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Stack;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.auth.CookieManager;
import com.alogic.auth.Session;
import com.alogic.auth.SessionManager;
import com.alogic.auth.SessionManagerFactory;
import com.alogic.cas.CasConstants;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;
import com.anysoft.webloader.ServletConfigProperties;
import com.anysoft.webloader.ServletHandler;
import com.logicbus.backend.Context;
import com.logicbus.backend.server.http.HttpContext;

/**
 * CAS服务端处理逻辑
 * 
 * @author yyduan
 * @since 1.6.11.60 [20180912 duanyy]
 * 
 * @version 1.6.11.61 [20180913 duanyy] <br>
 * - 部分字符串采用常量表达 <br>
 */
public class CasHandler implements ServletHandler,XMLConfigurable,Configurable,CasConstants{
	/**
	 * a logger of slf4j
	 */
	protected final static Logger LOG = LoggerFactory.getLogger(CasHandler.class);
	
	/**
	 * 缺省配置文件
	 */
	protected static final String DEFAULT = "java:///conf/alogic.cas.server.xml#App";
	
	/**
	 * 退出登录的请求参数名
	 */
	protected String arguLogout = CasConstants.ARGU_LOGOUT_REQUEST;
	
	/**
	 * ticket参数名
	 */
	protected String arguTicket = CasConstants.ARGU_TICKET;
	
	/**
	 * service参数名
	 */
	protected String arguService = CasConstants.ARGU_SERVICE;
	
	/**
	 * 编码
	 */
	protected String encoding = "utf-8";	
	
	/**
	 * command的前缀
	 */
	protected String cmdPrefix = "/cas";
	
	/**
	 * 内部跳转URL的参数
	 */
	protected String returnURL = CasConstants.ARGU_RETURNURL;	
	
	protected String sessionGroup = "$cas-server";	
	
	/**
	 * 本服务器的登录地址
	 */
	protected String loginURL = "/login";
	
	/**
	 * 当验证token时执行脚本
	 */
	protected Logiclet onValidate = null;
	
	/**
	 * 当注销时执行脚本
	 */
	protected Logiclet onLogout = null;		
	
	/**
	 * 当登录时执行脚本
	 */
	protected Logiclet onLogin = null;		
	
	protected String contentType = "text/xml;charset=utf-8";
	
	@Override
	public void configure(Properties props) {
		cmdPrefix = PropertiesConstants.getString(props, "cmdPrefix",cmdPrefix);
		returnURL = PropertiesConstants.getString(props,"auth.para.url",returnURL);
		loginURL = PropertiesConstants.getString(props, "auth.page.login", loginURL);
		arguTicket = PropertiesConstants.getString(props, "cas.para.ticket", arguTicket);
		arguService = PropertiesConstants.getString(props, "cas.para.service", arguService);
		arguLogout = PropertiesConstants.getString(props, "cas.para.logout",arguLogout);
		encoding = PropertiesConstants.getString(props,"http.encoding",encoding);		
		sessionGroup = PropertiesConstants.getString(props, "cas.server.group",sessionGroup);	
	}

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);		
		
		Element elem = XmlTools.getFirstElementByPath(e, "on-validate");
		if (elem != null){
			onValidate = Script.create(elem, props);
		}
		
		elem = XmlTools.getFirstElementByPath(e, "on-logout");
		if (elem != null){
			onLogout = Script.create(elem, props);
		}		
		
		elem = XmlTools.getFirstElementByPath(e, "on-login");
		if (elem != null){
			onLogin = Script.create(elem, props);
		}			
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		ServletConfigProperties props = new ServletConfigProperties(servletConfig);
		String master = PropertiesConstants.getString(props, "cas.server.master", DEFAULT);
		String secondary = PropertiesConstants.getString(props, "cas.server.secondary", DEFAULT);
		ResourceFactory rf = Settings.getResourceFactory();

		InputStream in = null;
		try {
			in = rf.load(master, secondary, null);
			Document doc = XmlTools.loadFromInputStream(in);
			if (doc != null){
				configure(doc.getDocumentElement(), props);
			}
		}catch (Exception ex){
			LOG.error("Can not init gateway with file : " + master);
		}finally{
			IOTools.close(in);
		}	
	}

	@Override
	public void doService(HttpServletRequest httpReq,HttpServletResponse httpResp,String method)throws ServletException, IOException {
		SessionManager sm = SessionManagerFactory.getDefault();
		Session session = sm.getSession(httpReq,httpResp,true);
		
		try {
			String cmd = getCommand(httpReq.getRequestURI());
			if (StringUtils.isNotEmpty(cmd)){					
				if (cmd.startsWith("/logout")){
					doLogout(httpReq,httpResp,sm,session);
					return;
				}
				
				if (cmd.startsWith("/validate")){
					doValidate(httpReq,httpResp,sm,session);
					return;
				}
				
				if (cmd.startsWith("/serviceValidate")){
					doValidate(httpReq,httpResp,sm,session);
					return;					
				}
				
				if (cmd.startsWith("/login")){
					doLogin(httpReq,httpResp,sm,session);
					return;
				}
			}
			doDefault(httpReq,httpResp,sm,session);
		}catch (BaseException ex){
			httpResp.sendError(E404,String.format("%s:%s",ex.getCode(),ex.getMessage()));
		}
	}
	
	protected void doDefault(HttpServletRequest httpReq,
			HttpServletResponse httpResp, SessionManager sm, Session session){
		try {
			httpResp.sendRedirect(loginURL);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

	protected void doLogin(HttpServletRequest httpReq,
			HttpServletResponse httpResp, SessionManager sm, Session session) {
		if (onLogin == null){
			throw new BaseException("core.e1000","onLogin is not defined");
		}
		if (!session.isLoggedIn()){
			//没有登录，跳转登录页面
			String service = getParameter(httpReq,this.arguService,"");
			if (StringUtils.isEmpty(service)){
				//没有找到跳转的service
				doDefault(httpReq,httpResp,sm,session);
				return;
			}
			
			//将service保存在会话中
			session.hSet(sessionGroup, this.arguService, service, true);
			
			try {
				String redirectUrl = loginURL;
				if (redirectUrl.indexOf("?") >= 0){
					redirectUrl += String.format("&%s=%s", this.returnURL,URLEncoder.encode(httpReq.getRequestURI(),encoding));
				}else{
					redirectUrl += String.format("?%s=%s", this.returnURL,URLEncoder.encode(httpReq.getRequestURI(),encoding));
				}
				httpResp.sendRedirect(redirectUrl);
			}catch (Exception ex){
				LOG.error(ex.getMessage());
			}
		}else{
			//已经登录
			String service = getParameter(httpReq,this.arguService,"");
			if (StringUtils.isEmpty(service)){
				service = session.hGet(sessionGroup, this.arguService, "");
			}			
			
			if (StringUtils.isEmpty(service)){
				//没有找到跳转的service
				doDefault(httpReq,httpResp,sm,session);
				return;
			}
			
			String ticket = session.getId();			
			Context ctx = new HttpContext(httpReq,httpResp,encoding);		
			LogicletContext logicletContext = new Context.ServantLogicletContext(ctx);	
			CookieManager cm = new CookieManager.Default(sm, httpReq, httpResp);
			try {
				logicletContext.setObject(ID_SESSION, session);		
				logicletContext.setObject(ID_COOKIES, cm) ;
				logicletContext.SetValue(ID_CAS_TICKET, ticket);
				logicletContext.SetValue(ID_CAS_SERVICE, service);
				
				XsObject doc = new JsonObject("root",new HashMap<String,Object>());
				onLogin.execute(doc,doc, logicletContext, null);
				
				ticket = PropertiesConstants.getString(logicletContext,ID_CAS_TICKET,session.getId());
			}finally{
				logicletContext.removeObject(ID_SESSION);
				logicletContext.removeObject(ID_COOKIES);
			}
			
			String redirectUrl = service;
			if (redirectUrl.indexOf("?") >= 0){
				redirectUrl += String.format("&%s=%s", this.arguTicket,ticket);
			}else{
				redirectUrl += String.format("?%s=%s", this.arguTicket,ticket);
			}
			try {
				httpResp.sendRedirect(redirectUrl);
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
		}
	}

	protected void doValidate(HttpServletRequest httpReq,HttpServletResponse httpResp, SessionManager sm, Session session) {
		if (onValidate == null){
			throw new BaseException("core.e1000","onValidate is not defined");
		}
		String service = getParameter(httpReq,this.arguService,"");
		String ticket = getParameter(httpReq,this.arguTicket,"");		
		Context ctx = new HttpContext(httpReq,httpResp,encoding);		
		LogicletContext logicletContext = new Context.ServantLogicletContext(ctx);	
		CookieManager cm = new CookieManager.Default(sm, httpReq, httpResp);
		try {
			logicletContext.setObject(ID_SESSION, session);		
			logicletContext.setObject(ID_COOKIES, cm) ;
			logicletContext.SetValue(ID_CAS_TICKET, ticket);
			logicletContext.SetValue(ID_CAS_SERVICE, service);
			
			String idXmlStack = "$xml-stack";
			Stack<Element> stack = new Stack<Element>();
			try {
				Document xmldoc = getInitXmlDocument();		
				stack.add(xmldoc.getDocumentElement());
				logicletContext.setObject(idXmlStack, stack);
				XsObject doc = new JsonObject("root",new HashMap<String,Object>());
				onValidate.execute(doc,doc, logicletContext, null);
				OutputStream out = null;
				try {
					ctx.setResponseContentType(contentType);
					out = ctx.getOutputStream();
					XmlTools.saveToOutputStream(xmldoc, out);
					out.flush();
				} catch (Exception ex) {
					LOG.error("Error when writing data to outputstream",ex);
				}finally {
					IOTools.close(out);
				}
			}finally{
				stack.pop();
				logicletContext.removeObject(idXmlStack);
			}
		}finally{
			logicletContext.removeObject(ID_SESSION);
			logicletContext.removeObject(ID_COOKIES);
		}
	}
	
	protected static Document getInitXmlDocument(){
		try {
			Document doc = XmlTools.newDocument();
			Element root = doc.createElementNS("http://www.yale.edu/tp/cas","cas:serviceResponse");
			doc.appendChild(root);
			return doc;
		} catch (ParserConfigurationException e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
			return null;
		}
	}
	
	protected void doLogout(HttpServletRequest httpReq,HttpServletResponse httpResp, SessionManager sm, Session session) {
		if (onLogout == null){
			throw new BaseException("core.e1000","onLogout is not defined");
		}
		Context ctx = new HttpContext(httpReq,httpResp,encoding);		
		LogicletContext logicletContext = new Context.ServantLogicletContext(ctx);	
		CookieManager cm = new CookieManager.Default(sm, httpReq, httpResp);
		try {
			logicletContext.setObject(ID_SESSION, session);		
			logicletContext.setObject(ID_COOKIES, cm) ;			
			XsObject doc = new JsonObject("root",new HashMap<String,Object>());
			onLogout.execute(doc,doc, logicletContext, null);			
		}finally{
			logicletContext.removeObject(ID_SESSION);
			logicletContext.removeObject(ID_COOKIES);
		}
		try {
			String redirectUrl = this.getParameter(httpReq, this.returnURL, this.loginURL);
			httpResp.sendRedirect(redirectUrl);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

	@Override
	public void destroy() {

	}
	
	/**
	 * 通过URI计算出当前的command
	 * @param uri URI
	 * @return cmd
	 */
	protected String getCommand(String uri){
		if (uri.startsWith(cmdPrefix)){
			return uri.substring(cmdPrefix.length());
		}else{
			return "";
		}
	}	
	
	protected String getParameter(HttpServletRequest request,String id,String dftValue){
		String value = request.getParameter(id);
		return StringUtils.isEmpty(value)?dftValue:value;
	}	

}
