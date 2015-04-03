package com.logicbus.backend;

import javax.servlet.http.HttpServletRequest;

import com.anysoft.util.Factory;
import com.anysoft.util.Settings;

/**
 * Session 管理器
 * 
 * @author duanyy
 * @since 1.1.3
 * 
 * @version 1.6.2.6 <br>
 * - 采用自己的Session替代HttpSession <br>
 * 
 * @version 1.6.3.12 [20150403 duanyy] <br>
 * - 可以从HttpServletRequest中直接创建Session <br>
 */
abstract public class SessionManager{
	
	/**
	 * 获取当前的Session
	 * 
	 * @param ctx 上下文
	 * @param create 当Session不存在的时候,而create为true，则创建一个Session
	 * @return Session
	 */
	abstract public Session getSession(Context ctx,boolean create) throws ServantException;
	
	/**
	 * 获取当前的Session，如果Session不存在，则创建一个
	 * @param ctx 上下文
	 * @return Session
	 */
	public Session getSession(Context ctx) throws ServantException{
		return getSession(ctx,true);
	}
	
	/**
	 * 直接通过HttpServletRequest获取当前的Session
	 * 
	 * @param request request
	 * @param create 当Session不存在的时候,而create为true，则创建一个Session
	 * @return Session
	 * @throws ServantException
	 */
	abstract public Session getSession(HttpServletRequest request,boolean create) throws ServantException;
	
	/**
	 * 直接通过HttpServletRequest获取当前的Session,如果Session不存在，则创建一个
	 * 
	 * @param request request
	 * @return Session
	 * @throws ServantException
	 */	
	public Session getSession(HttpServletRequest request) throws ServantException{
		return getSession(request,true);
	}
	
	private static SessionManager instance = null;
	
	synchronized static public SessionManager get(){
		if (instance == null){
			Settings settings = Settings.get();
			String module = settings.GetValue("session.manager", 
					"com.logicbus.backend.DefaultSessionManager");
			
			ClassLoader cl = (ClassLoader)settings.get("classLoader");
			TheFactory factory = new TheFactory(cl);
			
			instance = factory.newInstance(module, settings);
		}
		return instance;
	}
	
	/**
	 * 工厂类
	 * @author duanyy
	 *
	 */
	public static class TheFactory extends Factory<SessionManager>{
		public TheFactory(ClassLoader cl){
			super(cl);
		}
	}
}