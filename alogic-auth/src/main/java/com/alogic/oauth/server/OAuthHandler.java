package com.alogic.oauth.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.auth.CookieManager;
import com.alogic.auth.Session;
import com.alogic.auth.SessionManager;
import com.alogic.auth.SessionManagerFactory;
import com.alogic.oauth.OAuthConstants;
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
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import com.logicbus.backend.Context;
import com.logicbus.backend.server.http.HttpContext;

/**
 * OAuth2.0服务端处理
 * @author yyduan
 *
 * @since 1.6.11.62
 */
public class OAuthHandler implements ServletHandler,XMLConfigurable,Configurable,OAuthConstants{
	/**
	 * a logger of slf4j
	 */
	protected final static Logger LOG = LoggerFactory.getLogger(OAuthHandler.class);
	
	/**
	 * 缺省配置文件
	 */
	protected static final String DEFAULT = "java:///conf/alogic.oauth.server.xml#App";
	
	/**
	 * 编码
	 */
	protected String encoding = "utf-8";	
	
	/**
	 * command的前缀
	 */
	protected String cmdPrefix = "/oauth";
	
	/**
	 * 内部跳转URL的参数
	 */
	protected String returnURL = OAuthConstants.ARGU_RETURNURL;	
	
	/**
	 * redirect_uri参数
	 */
	protected String arguRedirectURI = OAuthConstants.ARGU_REDIRECT_URI;
	
	protected String sessionGroup = "$oauth-server";	
	
	protected String contentType = "application/json;charset=utf-8";
	
	protected static JsonProvider provider = JsonProviderFactory.createProvider();
	
	/**
	 * 本服务器的登录地址
	 */
	protected String loginURL = "/login";
	
	/**
	 * 当授权之后调用
	 */
	protected Logiclet onAuthorize = null;
	
	/**
	 * 当获取AccessToken时调用
	 */
	protected Logiclet onAccessToken = null;		
	
	/**
	 * 当获取RefreshToken时调用
	 */
	protected Logiclet onRefreshToken = null;	
	
	@Override
	public void configure(Properties props) {
		cmdPrefix = PropertiesConstants.getString(props, "cmdPrefix",cmdPrefix);
		returnURL = PropertiesConstants.getString(props,"auth.para.url",returnURL);
		loginURL = PropertiesConstants.getString(props, "auth.page.login", loginURL);
		arguRedirectURI = PropertiesConstants.getString(props, "oauth.para.redirectURI", arguRedirectURI);
		encoding = PropertiesConstants.getString(props,"http.encoding",encoding);		
		sessionGroup = PropertiesConstants.getString(props, "oauth.server.group",sessionGroup);	
	}

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);		
		
		Element elem = XmlTools.getFirstElementByPath(e, "on-authorize");
		if (elem != null){
			onAuthorize = Script.create(elem, props);
		}
		
		elem = XmlTools.getFirstElementByPath(e, "on-access-token");
		if (elem != null){
			onAccessToken = Script.create(elem, props);
		}		
		
		elem = XmlTools.getFirstElementByPath(e, "on-refresh-token");
		if (elem != null){
			onRefreshToken = Script.create(elem, props);
		}			
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		ServletConfigProperties props = new ServletConfigProperties(servletConfig);
		String master = PropertiesConstants.getString(props, "oauth.server.master", DEFAULT);
		String secondary = PropertiesConstants.getString(props, "oauth.server.secondary", DEFAULT);
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
				if (cmd.startsWith("/authorize")){
					//由浏览器重定向过来
					doAuthorize(httpReq,httpResp,sm,session);
					return;
				}
				
				if (cmd.startsWith("/access_token")){
					//由应用的后端调用
					doAccessToken(httpReq,httpResp,sm,session);
					return;
				}
				
				if (cmd.startsWith("/refresh_token")){
					doRefreshToken(httpReq,httpResp,sm,session);
					return;					
				}
			}
			doDefault(httpReq,httpResp,sm,session);
		}catch (BaseException ex){
			httpResp.sendError(E404,String.format("%s:%s",ex.getCode(),ex.getMessage()));
		}
	}
	
	protected void doAuthorize(HttpServletRequest httpReq,
			HttpServletResponse httpResp, SessionManager sm, Session session){
		if (onAuthorize == null){
			throw new BaseException("core.e1000","onAuthorize is not defined");
		}
		
		Context ctx = new HttpContext(httpReq,httpResp,encoding);		
		LogicletContext logicletContext = new Context.ServantLogicletContext(ctx);	
		CookieManager cm = new CookieManager.Default(sm, httpReq, httpResp);
		try {
			logicletContext.setObject(ID_SESSION, session);		
			logicletContext.setObject(ID_COOKIES, cm) ;
			
			XsObject doc = new JsonObject("root",new HashMap<String,Object>());
			onAuthorize.execute(doc,doc, logicletContext, null);
			
			try {
				String redirectURL = PropertiesConstants.getString(logicletContext,"$redirectUrl","");
				if (StringUtils.isEmpty(redirectURL)){
					httpResp.sendError(E404,"core.e1000:redirect url is unknown.");
				}else{
					httpResp.sendRedirect(redirectURL);
				}
			}catch (IOException e) {
				LOG.error(e.getMessage());
			}
		}finally{
			logicletContext.removeObject(ID_SESSION);
			logicletContext.removeObject(ID_COOKIES);
		}
	}
	
	protected void doAccessToken(HttpServletRequest httpReq,
			HttpServletResponse httpResp, SessionManager sm, Session session){
		if (onAccessToken == null){
			throw new BaseException("core.e1000","onAccessToken is not defined");
		}
		
		Context ctx = new HttpContext(httpReq,httpResp,encoding);		
		LogicletContext logicletContext = new Context.ServantLogicletContext(ctx);	
		CookieManager cm = new CookieManager.Default(sm, httpReq, httpResp);
		try {
			logicletContext.setObject(ID_SESSION, session);		
			logicletContext.setObject(ID_COOKIES, cm) ;
			
			XsObject doc = new JsonObject("root",new HashMap<String,Object>());
			onAccessToken.execute(doc,doc, logicletContext, null);
			
			OutputStream out = null;
			try {
				String data = provider.toJson(doc.getContent());
				ctx.setResponseContentType(contentType);
				out = ctx.getOutputStream();
				byte [] bytes = data.getBytes(ctx.getEncoding());
				ctx.setResponseContentLength(bytes.length);	
				Context.writeToOutpuStream(out, bytes);
				out.flush();
			} catch (Exception ex) {
				LOG.error("Error when writing data to outputstream",ex);
			}finally {
				IOTools.close(out);
			}			
		}finally{
			logicletContext.removeObject(ID_SESSION);
			logicletContext.removeObject(ID_COOKIES);
		}
	}
	
	protected void doRefreshToken(HttpServletRequest httpReq,
			HttpServletResponse httpResp, SessionManager sm, Session session){
		if (onRefreshToken == null){
			throw new BaseException("core.e1000","onAccessToken is not defined");
		}
		
		Context ctx = new HttpContext(httpReq,httpResp,encoding);		
		LogicletContext logicletContext = new Context.ServantLogicletContext(ctx);	
		CookieManager cm = new CookieManager.Default(sm, httpReq, httpResp);
		try {
			logicletContext.setObject(ID_SESSION, session);		
			logicletContext.setObject(ID_COOKIES, cm) ;
			
			XsObject doc = new JsonObject("root",new HashMap<String,Object>());
			onRefreshToken.execute(doc,doc, logicletContext, null);			
			OutputStream out = null;
			try {
				String data = provider.toJson(doc.getContent());
				ctx.setResponseContentType(contentType);
				out = ctx.getOutputStream();
				byte [] bytes = data.getBytes(ctx.getEncoding());
				ctx.setResponseContentLength(bytes.length);	
				Context.writeToOutpuStream(out, bytes);
				out.flush();
			} catch (Exception ex) {
				LOG.error("Error when writing data to outputstream",ex);
			}finally {
				IOTools.close(out);
			}			
		}finally{
			logicletContext.removeObject(ID_SESSION);
			logicletContext.removeObject(ID_COOKIES);
		}
	}		
	
	protected void doDefault(HttpServletRequest httpReq,
			HttpServletResponse httpResp, SessionManager sm, Session session){
		try {
			httpResp.sendError(E404,"core.e1000:Function is not supported now.");
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

