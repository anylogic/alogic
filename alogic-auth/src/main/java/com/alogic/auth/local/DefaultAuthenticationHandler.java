package com.alogic.auth.local;

import javax.servlet.ServletRequest;

import org.w3c.dom.Element;

import com.alogic.auth.AuthenticationHandler;
import com.alogic.auth.Principal;
import com.alogic.auth.SessionManager;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;

/**
 * AuthenticationHandler的缺省实现
 * 
 * <p>
 * 本实现从配置文件中读取用户信息进行验证
 * 
 * @author duanyy
 * @since 1.6.10.10
 */
public class DefaultAuthenticationHandler extends AuthenticationHandler.Abstract{
	protected SessionManager sessionManager = null;
		
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
	}
	
	@Override
	public Principal getCurrent(ServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal login(ServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPrivilege(Principal principal, String privilege) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void logout(Principal principal) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setSessionManager(SessionManager sm){
		this.sessionManager = sm;
	}
}
