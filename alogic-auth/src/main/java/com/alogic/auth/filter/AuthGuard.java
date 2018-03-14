package com.alogic.auth.filter;

import java.io.IOException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alogic.auth.Principal;
import com.alogic.auth.PrincipalManager;
import com.alogic.auth.Session;
import com.alogic.auth.SessionManagerFactory;
import com.alogic.remote.HttpConstants;
import com.anysoft.util.BaseException;
import com.anysoft.util.PropertiesConstants;
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
 * @version 1.6.11.14 [duanyy 20180129] <br>
 * - 增加auth.force配置参数,允许页面半保护状态； <br>
 * 
 * @version 1.6.11.22 [duanyy 20180314] <br>
 * - 优化URL处理 <br>
 */
public class AuthGuard implements Filter{
	/**
	 * a logger of log4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(AuthGuard.class);

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
	 * 当没有登录时，是否强制重定向到登录页面
	 */
	protected boolean forceLogin = true;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		FilterConfigProperties props = new FilterConfigProperties(filterConfig);
		loginPage = normalize(PropertiesConstants.getString(props,"auth.page.login",loginPage));
		returnURL = PropertiesConstants.getString(props,"auth.para.url",returnURL);
		encoding = PropertiesConstants.getString(props,"http.encoding",encoding);
		forceLogin = PropertiesConstants.getBoolean(props,"auth.force",forceLogin);	
	}

	protected String normalize(String url) {
		return url.indexOf("?") < 0 ? (url + "?true"):url;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) 
			throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest)request;
		HttpServletResponse httpResp = (HttpServletResponse)response;
		PrincipalManager sm = (PrincipalManager)SessionManagerFactory.getDefault();
		Session session = sm.getSession(httpReq,httpResp,true);
		
		if (session.isLoggedIn()){
			//已经登录
			chain.doFilter(request, response);
		}else{
			try {
				Principal principal = sm.getCurrent(httpReq,httpResp,session);
				if (principal == null && forceLogin){
					httpResp.sendRedirect(getLoginPage(getRequestURL(httpReq)));
				}else{
					chain.doFilter(request, response);
				}
			}catch (BaseException ex){
				httpResp.sendError(HttpConstants.E404, String.format("%s:%s", ex.getCode(),ex.getMessage()));
			}
		}
	}

	@Override
	public void destroy() {
		
	}
	
	protected String getLoginPage(String requestURL)throws IOException{	
		return String.format("%s&%s=%s",loginPage,returnURL,URLEncoder.encode(requestURL, encoding));
	}
	
	protected String getRequestURL(HttpServletRequest request){
		StringBuffer url = request.getRequestURL();
		String query = request.getQueryString();
		if (StringUtils.isNotEmpty(query)){
			url.append("?").append(getRequestURLQuery(query));
		}else{
			url.append("?true");
		}
		return url.toString();
	}	
	
	protected static String getRequestURLQuery(String data){		
		String fragment = "";
		String query = data;
		
		int index = data.indexOf("#");
		if (index >= 0){
			fragment = data.substring(index + 1);
			query = data.substring(0,index);
		}
		
		if (StringUtils.isNotEmpty(query)){
			String [] paras = query.split("[&]");
			StringBuffer buf = new StringBuffer();
			for (String para:paras){
				int idx = para.indexOf("=");
				
				if (idx >= 0){
					String k = para.substring(0, idx);
					String v = para.substring(idx + 1);
					
					if (k.equals("redirect") || k.equals("token")){
						continue;
					}
					
					buf.append(k);
					if (StringUtils.isNotEmpty(v)){
						buf.append("=").append(v);
					}
					buf.append("&");
				}else{
					if (StringUtils.isNotEmpty(para) && !para.equals("redirect") && !para.equals("token")){
						buf.append(para).append("&");
					}					
				}
			}
			query = buf.toString();
			if (StringUtils.isNotEmpty(query)){
				query = query.substring(0, query.length() - 1);
			}
		}
		
		return StringUtils.isEmpty(fragment) ? query : String.format("%s#%s", query,fragment);
	}
}
