package com.alogic.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.logicbus.backend.Context;
import com.logicbus.backend.server.http.HttpContext;

/**
 * 利用LocalSession实现的SessionManager
 * 
 * @author duanyy
 * @since 1.6.10.10
 */
public class LocalSessionManager implements SessionManager{

	@Override
	public void configure(Properties p) {
		
	}

	@Override
	public void configure(Element e, Properties p) {
		XmlElementProperties props = new XmlElementProperties(e,p);
		configure(props);
	}

	@Override
	public Session getSession(Context ctx, boolean create) {
		if (!(ctx instanceof HttpContext)){
			throw new BaseException("core.e1002","The Context is not a HttpContext instance.");
		}
		
		HttpContext httpContext = (HttpContext)ctx;
		HttpServletRequest request = httpContext.getRequest();

		return getSession(request,create);
	}

	@Override
	public Session getSession(HttpServletRequest request, boolean create) {
		HttpSession session = request.getSession(create);
		return session == null?null:new LocalSession(session);
	}

}
