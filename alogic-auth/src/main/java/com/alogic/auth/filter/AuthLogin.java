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

import com.alogic.auth.Principal;
import com.alogic.auth.PrincipalManager;
import com.alogic.auth.Session;
import com.alogic.auth.SessionManagerFactory;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.webloader.FilterConfigProperties;

/**
 * AuthLogin
 * 
 * <p>
 * AuthLogin是一个标准的Filter，用来处理登录页面的重定向。在已经登录的情况下，要重定向到原页面.
 * 
 * @author yyduan
 * @since 1.6.10.10
 * 
 * @version 1.6.11.14 [duanyy 20180129] <br>
 * - 增加redirectURL的容错； <br>
 * 
 * @version 1.6.11.15 [duanyy 20180206] <br>
 * - 对重定向的URL进行容错处理 <br>
 * 
 * @version 1.6.11.22 [duanyy 20180314] <br>
 * - 优化URL处理 <br>
 */
public class AuthLogin implements Filter{
	/**
	 * 重定向的参数名
	 */
	protected String returnURL = "returnURL";
	
	/**
	 * 没有登录的情况下，重定向的登录页面的地址
	 */
	protected String loginPage = "/login";
	
	/**
	 * 编码
	 */
	protected String encoding = "utf-8";
	
	/**
	 * token的参数名
	 */
	protected String token = "token";
	
	/**
	 * 成功登录之后的缺省页面
	 */
	protected String mainPage = "";
	
	/**
	 * 当没有登录时，是否强制重定向到登录页面
	 */
	protected boolean forceLogin = true;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		FilterConfigProperties props = new FilterConfigProperties(filterConfig);
		returnURL = PropertiesConstants.getString(props,"auth.para.url",returnURL);
		mainPage = PropertiesConstants.getString(props,"auth.page.main",mainPage);
		token = PropertiesConstants.getString(props,"auth.para.token",token);
		encoding = PropertiesConstants.getString(props,"http.encoding",encoding);
		loginPage = normalize(PropertiesConstants.getString(props,"auth.page.login",loginPage));
		forceLogin = PropertiesConstants.getBoolean(props,"auth.force",forceLogin);	
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) 
			throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest)request;
		HttpServletResponse httpResp = (HttpServletResponse)response;
		PrincipalManager sm = (PrincipalManager)SessionManagerFactory.getDefault();
		if (sm.isLocalLoginMode()){
			//本地登录模式，校验登录之后，由本地页面处理
			Session sess = sm.getSession(httpReq,httpResp, true);
			if (sess.isLoggedIn()){
				//已经登录
				String redirectURL = getParameter(httpReq,returnURL,mainPage);
				if (StringUtils.isNotEmpty(redirectURL)){
					//客户端提供了参数returnURL
					Principal principal = sm.getCurrent(httpReq,httpResp,sess);
					if (principal != null){
						String url = getRedirectURL(redirectURL,this.token,principal.getId());
						httpResp.sendRedirect(url);
					}else{
						chain.doFilter(request, response);
					}
				}else{
					chain.doFilter(request, response);
				}
			}else{
				chain.doFilter(request, response);
			}			
		}else{
			//非本地登录模式
			String redirectURL = getParameter(httpReq,returnURL,mainPage);			
			Session sess = sm.getSession(httpReq,httpResp, true);
			if (sess.isLoggedIn()){
				//已经登录，直接重定向到
				httpResp.sendRedirect(redirectURL);
			}else{
				//没有登录提交远端登录
				httpResp.sendRedirect(getLoginPage(redirectURL));
			}
		}
	}
		
	protected String normalize(String url) {
		return url.indexOf("?") < 0 ? (url + "?true"):url;
	}

	protected static String getRedirectURL(String redirectURL,String key,String tokenId)  {
		int fragment = redirectURL.indexOf("#");
		String token = String.format("?redirect&%s=%s&",key,tokenId);
		if (fragment < 0){
			//如果没有fragment
			int query = redirectURL.indexOf("?");
			if (query < 0){
				//也没有query
				return redirectURL + token;
			}else{
				return redirectURL.substring(0, query) + token + redirectURL.substring(query + 1);
			}
		}else{
			//如果有fragment
			int query = redirectURL.indexOf("?");
			if (query >= 0 && query < fragment){
				//query在fragment之前，是真正的query
				return redirectURL.substring(0, query) + token + redirectURL.substring(query + 1);
			}else{
				//query在fragment之后，是fragment的query
				return redirectURL.substring(0, fragment) + token + redirectURL.substring(fragment);
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
			query = query.substring(0, query.length() - 1);
		}
		
		return StringUtils.isEmpty(fragment) ? query : String.format("%s#%s", query,fragment);
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