package com.alogic.together2.service;

import com.alogic.xscript.LogicletContext;
import com.logicbus.backend.Context;

/**
 * 服务环境下的logiclet上下文
 * 
 * @author duanyy
 * @since 1.6.11.3
 * 
 * 
 */
public class SevantLogicletContext extends LogicletContext {

	/**
	 * 客户端ip
	 */
	public static final String CLIENTIP = "$clientIp";
	
	/**
	 * 真实的客户端ip
	 */
	public static final String CLIENTIPREAL = "$clientIpReal";
	
	/**
	 * 本次服务的全局序列号
	 */
	public static final String SN = "$sn";
	
	/**
	 * 主机信息
	 */
	public static final String HOST = "$host";
	
	/**
	 * 主机域名
	 */
	public static final String HOST_DOMAIN = "$hostdomain";	
	
	/**
	 * 请求方法
	 */
	public static final String METHOD = "$method";
	
	/**
	 * 请求参数
	 */
	public static final String QUERY = "$query";
	
	/**
	 * 请求完整的URI
	 */
	public static final String URI = "$uri";
	
	/**
	 * 请求完整的URL
	 */
	public static final String URL = "$url";
	
	/**
	 * 请求的路径
	 */
	public static final String PATH = "$path";
	
	protected Context context = null;

	public SevantLogicletContext(Context ctx) {
		super(ctx);
		context = ctx;
	}
	
	@Override
	protected String _GetValue(String name) {
		System.out.println("var name = " + name);
		if (context != null){
			switch(name){
				case CLIENTIP:
					return context.getClientIp();
				case CLIENTIPREAL:
					return context.getClientRealIp();
				case SN:
					return context.getGlobalSerial();
				case HOST:
					return context.getHost();
				case METHOD:
					return context.getMethod();
				case QUERY:
					return context.getQueryString();
				case URI:
					return context.getRequestURI();
				case PATH:
					return context.getPathInfo();
				case URL:
					return context.getRequestURL();
				case HOST_DOMAIN:
					return context.getHostDomain();					
			}
		}
		
		return super._GetValue(name);
	}	

}
