package com.alogic.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.logicbus.backend.Context;
import com.logicbus.backend.server.http.HttpContext;

/**
 * 会话管理器
 * 
 * @author duanyy
 * @since 1.6.10.10
 * 
 * @version 1.6.11.7 [20180107 duanyy] <br>
 * - 优化Session管理 <br>
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
	
	/**
	 * 根据Id来获取Session对象
	 * @param sessionId id
	 * @param create 是否创建
	 * @return Session对象
	 */
	public Session getSession(String sessionId,boolean create);
	
	/**
	 * 根据id来删除Session对象
	 * @param sessionId id
	 */
	public void delSession(String sessionId);
	
	/**
	 * 虚基类
	 * @author duanyy
	 *
	 */
	public abstract static class Abstract implements SessionManager{	
		/**
		 * a logger of slf4j
		 */
		protected static final Logger LOG = LoggerFactory.getLogger(SessionManager.class);
		
		/**
		 * 会话的生存期
		 */
		protected long ttl = 30 * 60 * 1000L;
		
		@Override
		public void configure(Properties p) {
			ttl = PropertiesConstants.getLong(p,"ttl", ttl);
		}

		@Override
		public void configure(Element e, Properties p) {
			XmlElementProperties props = new XmlElementProperties(e,p);
			configure(props);
		}
		
		/**
		 * 获取当前的SessionId
		 * @param request HttpServletRequest
		 * @param create 是否创建
		 * @return 当前的SessionId
		 */
		protected String getSessionId(HttpServletRequest request, boolean create){
			HttpSession httpSession = request.getSession(create);
			return httpSession == null ? null : httpSession.getId();
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
			String sessionId = getSessionId(request,create);
			return StringUtils.isNotEmpty(sessionId)?getSession(sessionId,create):null;
		}

		protected boolean isExpired(Session sess){
			return System.currentTimeMillis() - sess.getTimestamp() > ttl;
		}		
	}
	
	public static class SessionCleaner implements HttpSessionListener{
		/**
		 * a logger of slf4j
		 */
		protected static final Logger LOG = LoggerFactory.getLogger(SessionManager.class);
		
		@Override
		public void sessionCreated(HttpSessionEvent se) {
			LOG.info(String.format("Session %s is created", se.getSession().getId()));
		}

		@Override
		public void sessionDestroyed(HttpSessionEvent se) {
			HttpSession sess = se.getSession();
			if (sess != null){
				LOG.info(String.format("Session %s has been destroyed.", sess.getId()));
				SessionManager sm = SessionManagerFactory.getDefault();
				sm.delSession(sess.getId());
			}
		}
		
	}
}
