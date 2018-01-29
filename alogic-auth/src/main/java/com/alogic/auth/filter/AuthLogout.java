package com.alogic.auth.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
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
 */
public class AuthLogout implements Filter{
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
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		FilterConfigProperties props = new FilterConfigProperties(filterConfig);
		returnURL = PropertiesConstants.getString(props,"auth.para.url",returnURL);
		mainPage = PropertiesConstants.getString(props,"auth.page.login",mainPage);
		encoding = PropertiesConstants.getString(props,"http.encoding",encoding);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) 
			throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest)request;
		HttpServletResponse httpResp = (HttpServletResponse)response;
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
		httpResp.sendRedirect(StringUtils.isEmpty(redirectURL)?mainPage:redirectURL);
	}

	@Override
	public void destroy() {
		
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