package com.alogic.auth;

import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Element;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.logicbus.backend.Context;
import com.logicbus.backend.server.http.HttpContext;

/**
 * Principal管理器
 * @author yyduan
 * @since 1.6.10.10
 */
public interface PrincipalManager extends SessionManager,AuthenticationHandler{
	
	/**
	 * 虚基类
	 * @author yyduan
	 *
	 */
	public abstract static class Abstract extends AuthenticationHandler.Abstract implements PrincipalManager{
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}
		
		@Override
		public void configure(Properties p){
			
		}
		
		@Override
		public Session getSession(Context ctx, boolean create) {
			if (!(ctx instanceof HttpContext)){
				throw new BaseException("core.nothttpcontext","The Context is not a HttpContext instance.");
			}
			
			HttpContext httpContext = (HttpContext)ctx;
			HttpServletRequest request = httpContext.getRequest();

			return getSession(request,create);
		}
	}
}
