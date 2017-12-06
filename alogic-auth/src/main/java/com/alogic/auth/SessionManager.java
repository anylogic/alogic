package com.alogic.auth;

import javax.servlet.http.HttpServletRequest;

import com.anysoft.util.Configurable;
import com.anysoft.util.XMLConfigurable;
import com.logicbus.backend.Context;

/**
 * 会话管理器
 * 
 * @author duanyy
 * @since 1.6.10.10
 */
public interface SessionManager extends Configurable,XMLConfigurable{
	
	/**
	 * 从Context中获取Session
	 * 
	 * <p>
	 * 用来从服务调用Context中获取当前的Session实例，当当前Session不存在的时候，如果create为true，则创建Session,反之返回为null.
	 * 
	 * @param ctx 服务调用上下文
	 * @param create 是否创建
	 * @return 当前的Session实例
	 */
	public Session getSession(Context ctx,boolean create);
	
	/**
	 * 从Request中获取Session
	 * 
	 * <p>
	 * 用来从HttpServletRequest中获取当前的Session实例，当当前Session不存在的时候，如果create为true，则创建Session,反之返回为null.
	 * 
	 * @param request HttpServletRequest
	 * @param create 是否创建
	 * @return 当前的Session实例
	 */
	public Session getSession(HttpServletRequest request,boolean create);
}
