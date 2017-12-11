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

import com.alogic.auth.Session;
import com.alogic.auth.SessionManager;
import com.alogic.auth.SessionManagerFactory;
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
 */
public class AuthGuard implements Filter{
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
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		FilterConfigProperties props = new FilterConfigProperties(filterConfig);
		loginPage = PropertiesConstants.getString(props,"auth.page.login",loginPage);
		returnURL = PropertiesConstants.getString(props,"auth.para.url",returnURL);
		encoding = PropertiesConstants.getString(props,"http.encoding",encoding);
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
			StringBuffer redirectURL = new StringBuffer(loginPage);
			redirectURL.append("?").append(returnURL).append("=");						
			redirectURL.append(URLEncoder.encode(getRequestURL(httpReq), encoding));		
			httpResp.sendRedirect(redirectURL.toString());
		}
	}

	@Override
	public void destroy() {
		
	}
	
	protected String getRequestURL(HttpServletRequest request){
		StringBuffer url = request.getRequestURL();
		String query = request.getQueryString();
		if (StringUtils.isNotEmpty(query)){
			url.append("?").append(query);
		}
		return url.toString();
	}
}
