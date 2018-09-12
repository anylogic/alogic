package com.alogic.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie管理器
 * 
 * @author yyduan
 *
 */
public interface CookieManager {
	/**
	 * 获取指定的Cookies值
	 * @param name Cookies名称
	 * @param dft 缺省值
	 * @return Cookies值
	 */
	public String getCookie(String name,String dft);	
	
	/**
	 * 设置Cookies
	 * @param name Cookies名称
	 * @param value 取值
	 */
	public void setCookie(String name,String value,String path,int ttl);
	
	/**
	 * 缺省实现
	 * @author yyduan
	 *
	 */
	public static class Default implements CookieManager{
		protected HttpServletRequest request = null;
		protected HttpServletResponse response = null;
		protected SessionManager sm = null;
		
		public Default(SessionManager sm,HttpServletRequest req,HttpServletResponse res){
			this.request = req;
			this.response = res;
			this.sm = sm;
		}

		@Override
		public String getCookie(String name, String dft) {
			return sm.getCookie(request, name, dft);
		}

		@Override
		public void setCookie(String name, String value, String path, int ttl) {
			sm.setCookie(response, name, value, path, ttl);
		}
	}
}
