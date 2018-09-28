package com.alogic.oauth.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alogic.auth.Session;
import com.alogic.auth.SessionManager;
import com.alogic.load.Loadable;
import com.alogic.oauth.OAuthConstants;
import com.anysoft.util.Configurable;
import com.anysoft.util.XMLConfigurable;

/**
 * 定义了OAuth2.0服务器的信息
 * 
 * @author yyduan
 * @since 1.6.11.61
 */
public interface OAuthServer extends OAuthConstants,Configurable,XMLConfigurable,Loadable{
	
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
	 * 处理用于登录的授权请求
	 * 
	 * @param httpReq request
	 * @param httpResp response
	 * @param sm 会话管理器
	 * @param session 当前session
	 */
	public void doLoginRequest(HttpServletRequest httpReq,HttpServletResponse httpResp,SessionManager sm,Session session);

	/**
	 * 处理用于帐号绑定的授权请求
	 * 
	 * @param httpReq request
	 * @param httpResp response
	 * @param sm 会话管理器
	 * @param session 当前session
	 */
	public void doBindRequest(HttpServletRequest httpReq,HttpServletResponse httpResp,SessionManager sm,Session session);

	/**
	 * 处理登录Callback
	 * 
	 * @param httpReq request
	 * @param httpResp response
	 * @param sm 会话管理器
	 * @param session 当前session
	 */	
	public void doLoginCallback(HttpServletRequest httpReq,HttpServletResponse httpResp,SessionManager sm,Session session);
	
	/**
	 * 处理绑定Callback
	 * 
	 * @param httpReq request
	 * @param httpResp response
	 * @param sm 会话管理器
	 * @param session 当前session
	 */	
	public void doBindCallback(HttpServletRequest httpReq,HttpServletResponse httpResp,SessionManager sm,Session session);	
	
}
