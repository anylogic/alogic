package com.logicbus.backend;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.anysoft.util.Properties;
import com.logicbus.backend.server.http.HttpContext;

/**
 * 缺省的SessionManager
 * 
 * <p>
 * 直接采用应用服务器的实现
 * 
 * @author duanyy
 * @since 1.1.3
 * @version 1.6.2.6
 * - 采用自己的Session替代HttpSession
 */
public class DefaultSessionManager extends SessionManager {
	
	/**
	 * 构造函数
	 * @param props 参数配置
	 * @since 1.2.0 修正无法创建的bug
	 */
	public DefaultSessionManager(Properties props){
		
	}
	
	public Session getSession(Context ctx, boolean create) throws ServantException{
		if (!(ctx instanceof HttpContext)){
			throw new ServantException("core.nothttpcontext","The Context is not a HttpContext instance.");
		}
		
		HttpContext httpContext = (HttpContext)ctx;
		
		HttpServletRequest request = httpContext.getRequest();
		HttpSession session = request.getSession(create);
		return session == null?null:new LocalSession(session);
	}

}
