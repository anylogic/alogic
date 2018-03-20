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

import com.alogic.auth.PrincipalManager;
import com.alogic.auth.Session;
import com.alogic.auth.SessionManagerFactory;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.webloader.FilterConfigProperties;

/**
 * AuthLogout
 * 
 * <p>
 * AuthLogout是一个标准的Filter，用来处理注销页面的重定向。
 * 
 * @author yyduan
 * @since 1.6.11.14
 * 
 * @version 1.6.11.22 [duanyy 20180314] <br>
 * - 优化URL处理 <br>
 * 
 * @version 1.6.11.23 [duanyy 20180320] <br>
 * - 修正某些不可配置的参数名 <br>
 */
public class AuthLogout implements Filter{
	/**
	 * a logger of slf4j
	 */
	protected final static Logger LOG = LoggerFactory.getLogger(AuthLogout.class);
	/**
	 * 重定向的参数名
	 */
	protected String returnURL = "returnURL";
	
	/**
	 * 编码
	 */
	protected String encoding = "utf-8";
	
	/**
	 * 注销之后的缺省页面
	 */
	protected String mainPage = "";
	
	protected String logoutPage = "";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		FilterConfigProperties props = new FilterConfigProperties(filterConfig);
		returnURL = PropertiesConstants.getString(props,"auth.para.url",returnURL);
		mainPage = PropertiesConstants.getString(props,"auth.page.login",mainPage);
		logoutPage = normalize(PropertiesConstants.getString(props,"auth.page.logout",logoutPage));
		encoding = PropertiesConstants.getString(props,"http.encoding",encoding);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) 
			throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest)request;
		HttpServletResponse httpResp = (HttpServletResponse)response;
		
		String callbackId = httpReq.getParameter("callback");
		
		if (StringUtils.isNotEmpty(callbackId)){
			//是由登录服务器从后台调用的,callbackId为其SessionId
			LOG.info("Callback from sso server,sessionId=" + callbackId);
			PrincipalManager sm = (PrincipalManager)SessionManagerFactory.getDefault();
			try {
				sm.delSession(callbackId);
			}catch (Exception ex){
				// nothing to do
			}
		}else{
			PrincipalManager sm = (PrincipalManager)SessionManagerFactory.getDefault();
			Session sess = sm.getSession(httpReq,httpResp, false);
			if (sess != null && sess.isLoggedIn()){
				try {
					sm.logout(httpReq, httpResp);
				}catch (Exception ex){
					// nothing to do
				}
			}
			
			String redirectURL = getParameter(httpReq,returnURL,mainPage);
			if (sm.isLocalLoginMode()){
				//本地模式下，直接重定向
				httpResp.sendRedirect(redirectURL);	
			}else{
				//客户端模式，重定向到服务端
				if (StringUtils.isNotEmpty(logoutPage)){
					httpResp.sendRedirect(getLogoutPage(redirectURL));	
				}else{
					chain.doFilter(request, response);
				}
			}
		}
	}

	@Override
	public void destroy() {
		
	}
	
	protected String normalize(String url) {
		return url.indexOf("?") < 0 ? (url + "?true"):url;
	}
	
	protected String getLogoutPage(String requestURL)throws IOException{	
		return String.format("%s&%s=%s",logoutPage,returnURL,URLEncoder.encode(requestURL, encoding));
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
}