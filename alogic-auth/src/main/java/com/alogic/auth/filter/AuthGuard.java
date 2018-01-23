package com.alogic.auth.filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.alogic.auth.Session;
import com.alogic.auth.SessionManager;
import com.alogic.auth.SessionManagerFactory;
import com.alogic.remote.Client;
import com.alogic.remote.backend.Backend;
import com.alogic.remote.httpclient.HttpClient;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;
import com.anysoft.webloader.FilterConfigProperties;

/**
 * AuthGuard
 * 
 * <p>
 * AuthGuard是一个标准的Filter，用来保护需要验证的页面区域
 * 
 * @author yyduan
 * @since 1.6.10.10
 * 
 * @version 1.6.11.2 [20171218 duanyy] <br>
 * - 在重定向登录页面的时候，支持集群负载均衡 <br>
 * 
 * @version 1.6.11.3 [20171219 duanyy] <br>
 * - 集群模式可通过开关开启 <br>
 * 
 */
public class AuthGuard implements Filter{
	/**
	 * a logger of log4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(AuthGuard.class);
	
	protected static final String DEFAULT = 
			"java:///com/ketty/util/app.proxy.xml#"+ AuthGuard.class.getName();	
	
	/**
	 * 没有登录的情况下，重定向的登录页面的地址
	 */
	protected String loginPage = "/login";
	
	/**
	 * 编码
	 */
	protected String encoding = "utf-8";
	
	/**
	 * 重定向的参数名
	 */
	protected String returnURL = "returnURL";
	
	/**
	 * remote client
	 */
	protected Client client = null;
	
	/**
	 * 定向页面的scheme
	 */
	protected String scheme = "http";
	
	/**
	 * 定向页面的路径
	 */
	protected String path = "";
	
	/**
	 * 登录服务器的应用id
	 */
	protected String app = "alogic-sso-server";
	
	/**
	 * 优先集群
	 */
	protected boolean clusterFirst = false;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		FilterConfigProperties props = new FilterConfigProperties(filterConfig);
		loginPage = PropertiesConstants.getString(props,"auth.page.login",loginPage);
		returnURL = PropertiesConstants.getString(props,"auth.para.url",returnURL);
		encoding = PropertiesConstants.getString(props,"http.encoding",encoding);
		scheme =  PropertiesConstants.getString(props, "auth.page.scheme", scheme);
		path = PropertiesConstants.getString(props, "auth.page.path", path);
		app = PropertiesConstants.getString(props, "auth.page.app", app);
		clusterFirst = PropertiesConstants.getBoolean(props,"auth.page.cluster",clusterFirst);
		
		String master = PropertiesConstants.getString(props, "app.proxy.master", DEFAULT);
		String secondary = PropertiesConstants.getString(props, "app.proxy.secondary", DEFAULT);
		
		if (clusterFirst){
			Document doc = loadDocument(master,secondary);
			if (doc != null){
				Factory<Client> f = new Factory<Client>();
				try {
					client = f.newInstance(doc.getDocumentElement(), props, "module", HttpClient.class.getName());
				}catch (Exception ex){
					LOG.error(String.format("Can not create remote client with %s",XmlTools.node2String(doc.getDocumentElement())),ex);
				}
			}
			if (client == null){
				client = new HttpClient();
				client.configure(props);			
				LOG.info(String.format("Using default remote client:%s",client.getClass().getName()));
			}
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) 
			throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest)request;
		SessionManager sm = SessionManagerFactory.getDefault();
		Session sess = sm.getSession(httpReq, true);
		if (sess.isLoggedIn()){
			//已经登录
			chain.doFilter(request, response);
		}else{
			//未登录，重定向到登录页面
			HttpServletResponse httpResp = (HttpServletResponse)response;
			StringBuffer redirectURL = new StringBuffer(getLoginPage(sess.getId(),loginPage));
			redirectURL.append("&").append(returnURL).append("=");						
			redirectURL.append(URLEncoder.encode(getRequestURL(httpReq), encoding));		
			httpResp.sendRedirect(redirectURL.toString());
		}
	}

	@Override
	public void destroy() {
		
	}
	
	/**
	 * 根据负载均衡策略获取登录页面的地址
	 * @param sessionId 会话id
	 * @param dftPage 缺省的页面
	 * @return 页面地址
	 */
	protected String getLoginPage(String sessionId,String dftPage){
		if (client == null){
			return dftPage;
		}
		try {
			Properties props = new DefaultProperties();			
			props.SetValue("$app", app);
			Backend backend = client.getBackend(sessionId, props, 0);
			
			if (backend == null){
				LOG.info("Can not find a valid backend to service,Using default:" + dftPage);
				return dftPage;
			}
		
			StringBuilder url = new StringBuilder();
			url.append(scheme)
				.append("://")
				.append(backend.getIp())
				.append(':')
				.append(backend.getPort());
			
			if (StringUtils.isNotEmpty(path)){
				url.append(path);
			}
			return url.toString();
		}catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
			return dftPage;
		}
	}
	
	protected String getRequestURL(HttpServletRequest request){
		StringBuffer url = request.getRequestURL();
		String query = request.getQueryString();
		if (StringUtils.isNotEmpty(query)){
			url.append("?").append(query);
		}else{
			url.append("?true");
		}
		return url.toString();
	}
	
	private Document loadDocument(String master, String secondary) {
		ResourceFactory rf = Settings.getResourceFactory();
		InputStream in = null;
		try{
			in = rf.load(master, secondary, null);
			return XmlTools.loadFromInputStream(in);
		}catch (Exception ex){
			LOG.error("Can not load app proxy config file" + master,ex);
		}finally{
			IOTools.close(in);
		}
		return null;
	}	
}
