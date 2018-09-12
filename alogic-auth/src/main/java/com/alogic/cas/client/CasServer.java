package com.alogic.cas.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alogic.auth.Session;
import com.alogic.auth.SessionManager;
import com.alogic.cas.CasConstants;
import com.alogic.load.Loadable;
import com.anysoft.util.Configurable;
import com.anysoft.util.XMLConfigurable;

/**
 * CasServer
 * 
 * <p>
 * 定义了Cas服务器的必要信息
 * 
 * @author yyduan
 * @since 1.6.11.60 [20180912 duanyy]
 */
public interface CasServer extends CasConstants,Configurable,XMLConfigurable,Loadable{
	
	/**
	 * 获取id
	 * @return id
	 */
	public String getId();
	
	/**
	 * 服务器配置是否有效
	 * @return true or false
	 */
	public boolean isOk();
	
	/**
	 * 处理Cas服务器注销事件
	 * @param httpReq request
	 * @param httpResp response
	 * @param sm 会话管理器
	 * @param session 当前session
	 * @param sessionId 会话id
	 */
	public void doLogoutCallback(HttpServletRequest httpReq,HttpServletResponse httpResp,SessionManager sm,Session session,String sessionId);
	
	/**
	 * 处理ticket验证事件
	 * @param httpReq request
	 * @param httpResp response
	 * @param sm 会话管理器
	 * @param session 当前session
	 */
	public void doValidate(HttpServletRequest httpReq,HttpServletResponse httpResp,SessionManager sm,Session session);
	
	/**
	 * 处理本地发起的注销事件
	 * @param httpReq request
	 * @param httpResp response
	 * @param sm 会话管理器
	 * @param session 当前session
	 */
	public void doLogout(HttpServletRequest httpReq,HttpServletResponse httpResp,SessionManager sm,Session session);
	
	/**
	 * 处理本地发起的登录事件
	 * @param httpReq request
	 * @param httpResp response
	 * @param sm 会话管理器
	 * @param session 当前session
	 */
	public void doLogin(HttpServletRequest httpReq,HttpServletResponse httpResp,SessionManager sm,Session session);
}
