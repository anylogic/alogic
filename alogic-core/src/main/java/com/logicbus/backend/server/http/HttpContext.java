package com.logicbus.backend.server.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.anysoft.util.Settings;
import com.logicbus.backend.Context;

/**
 * Http请求的上下文
 * 
 * @author duanyy
 * @version 1.0.5 [20140412 duanyy]  <br>
 * - 改进消息传递模型 <br>
 */

public class HttpContext extends Context {
	
	/**
	 * request
	 */
	protected HttpServletRequest request = null;
	
	/**
	 * to get request
	 * @return HttpServletRequest
	 */
	public HttpServletRequest getRequest(){ return request;}
	
	/**
	 * constructor
	 * @param _request HttpServletRequest
	 */
	public HttpContext(HttpServletRequest _request,String serial){
		super(serial);
		request = _request;
	}
	
	 
	public String _GetValue(String _name) {
		String found = super._GetValue(_name);
		if (found == null || found.length() <= 0){
			if (request != null)
			{
				HttpSession session = request.getSession(false);
				if (session != null){
					Object obj = session.getAttribute(_name);
					if (obj != null) return obj.toString();
				}
				String value = request.getParameter(_name);
				if (value != null){
					return value;
				}
			}
		}
		return found;
	}

	
	public String getClientIp() {
		/**
		 * 支持负载均衡器的X-Forwarded-For
		 */
		String ip = request.getHeader(ForwardedHeader);
		return (ip == null || ip.length() <= 0) ? request.getRemoteHost() : ip;
	}

	
	public String getHost() {
		return request.getLocalAddr() + ":" + request.getLocalPort();
	}

	
	public String getRequestURI() {
		// since 1.2.0 返回整个URL
		String queryString = request.getQueryString();
		if (queryString != null && queryString.length() > 0){
			return request.getRequestURL().toString() + "?" + queryString;
		}else{
			return request.getRequestURL().toString();
		}
	}
	
	public static String ForwardedHeader = "X-Forwarded-For";
	static{
		Settings settings = Settings.get();
		ForwardedHeader = settings.GetValue("http.forwardedheader", ForwardedHeader);
	}
}
