package com.logicbus.backend;

import javax.servlet.http.HttpSession;

import com.anysoft.util.Factory;
import com.anysoft.util.Settings;

/**
 * Session 管理器
 * 
 * @author duanyy
 * @since 1.1.3
 */
abstract public class SessionManager{
	
	/**
	 * 获取当前的Session
	 * 
	 * @param ctx 上下文
	 * @param create 当Session不存在的时候,而create为true，则创建一个Session
	 * @return Session
	 */
	abstract public HttpSession getSession(Context ctx,boolean create) throws ServantException;
	
	/**
	 * 获取当前的Session，如果Session不存在，则创建一个
	 * @param ctx 上下文
	 * @return Session
	 */
	public HttpSession getSession(Context ctx) throws ServantException{
		return getSession(ctx,true);
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