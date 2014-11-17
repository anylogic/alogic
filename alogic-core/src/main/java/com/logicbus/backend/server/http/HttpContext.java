package com.logicbus.backend.server.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.anysoft.util.Settings;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.Message;

/**
 * Http请求的上下文
 * 
 * @author duanyy
 * @version 1.0.5 [20140412 duanyy]  <br>
 * - 改进消息传递模型 <br>
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 将MessageDoc和Context进行合并整合 <br>
 * 
 */

public class HttpContext extends Context {
	/**
	 * a logger of log4j
	 */
	protected final Logger logger = LogManager.getLogger(HttpContext.class);
	
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
	 * response
	 */
	protected HttpServletResponse response = null;
	
	/**
	 * to get response
	 * @return HttpServletResponse
	 */
	public HttpServletResponse getResponse(){return response;}
	
	/**
	 * constructor
	 * @param _request HttpServletRequest
	 */
	public HttpContext(HttpServletRequest _request,HttpServletResponse _response,String _encoding){
		super(_encoding);
		request = _request;
		response = _response;
	}
	
	@Override
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

	@Override
	public String getClientIp() {
		/**
		 * 支持负载均衡器的X-Forwarded-For
		 */
		String ip = request.getHeader(ForwardedHeader);
		return (ip == null || ip.length() <= 0) ? request.getRemoteHost() : ip;
	}

	@Override
	public String getHost() {
		return request.getLocalAddr() + ":" + request.getLocalPort();
	}

	@Override
	public String getRequestURI() {
		// since 1.2.0 返回整个URL
		String queryString = request.getQueryString();
		if (queryString != null && queryString.length() > 0){
			return request.getRequestURL().toString() + "?" + queryString;
		}else{
			return request.getRequestURL().toString();
		}
	}

	private String globalSerial = null;
	
	@Override
	public String getGlobalSerial() {
		if (globalSerial == null || globalSerial.length() <= 0){
			globalSerial = request.getHeader("GlobalSerial");
			if (globalSerial == null || globalSerial.length() <= 0){
				globalSerial = createGlobalSerial();
			}
		}
		return globalSerial;
	}

	@Override
	public String getRequestHeader(String id) {
		return request.getHeader(id);
	}

	@Override
	public void setResponseHeader(String id, String value) {
		response.setHeader(id, value);
	}

	@Override
	public String getReqestContentType() {
		return request.getContentType();
	}
	
	protected void onMessageCreated(Message msg) {
		if (msg.doRead(this)){
			try {
				msg.read(request.getInputStream(), this);
			} catch (IOException e) {
				setReturn("core.fatalerror","Can not read from inputstream.");
				logger.error("Can not read from inputstream.",e);
			}
		}
	}
	
	@Override
	public void finish() {
		try {
			if (msg == null){
				response.sendError(404, "No message is found,check servant implemention.");
			}else{
				if (msg.doWrite(this)){
					response.setCharacterEncoding(encoding);
					response.setContentType(msg.getContentType(this));
					msg.write(response.getOutputStream(), this);
				}
			}
		}catch (Exception ex){
			try {
				response.sendError(404, ex.getMessage());
			}catch (Exception e){
				logger.error("Error when writing result",e);
			}
		}
	}
	
	public static String ForwardedHeader = "X-Forwarded-For";
	static{
		Settings settings = Settings.get();
		ForwardedHeader = settings.GetValue("http.forwardedheader", ForwardedHeader);
	}
}
