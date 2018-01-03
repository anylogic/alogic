package com.alogic.cache.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.alogic.cache.core.MultiFieldObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;
import com.logicbus.backend.Session;

/**
 * 分布式环境下的会话管理器
 * 
 * @author duanyy
 * @deprecated
 */
public class DistributedSessionManager extends CachedSessionManager {
	/**
	 * DES3编码器
	 */
	protected Coder encrypter = null;	
	protected long sessionTimeout = 30;
	public DistributedSessionManager(Properties p) {
		super(p);
		encrypter = CoderFactory.newCoder("DES3");		
		sessionTimeout = PropertiesConstants.getLong(p,"session.timeout", sessionTimeout);
	}

	@Override
	public Session getSession(HttpServletRequest request, boolean create){
		Session session = null;
		
		String token = request.getParameter("token");
		if (token != null && token.length() > 0){
			//有令牌传入
			String sessionId = encrypter.decode(token, getClientIp(request));
			if (!sessionId.equals(token)){
				synchronized (this){
					if (cache == null){
						cache = getCacheStore();
					}
					MultiFieldObject sessionObject = cache.get(sessionId, true);
					if (sessionObject != null){
						HttpSession httpSession = request.getSession(true);
						session = new CachedSession(sessionObject,httpSession);
					}else{
						//不存在
						HttpSession httpSession = request.getSession(create);
						if (httpSession != null){
							CachedSessionObject newObject = new CachedSessionObject(sessionId);
							sessionObject = cache.set(sessionId, newObject);
							session = new CachedSession(sessionObject,httpSession);
						}
					}
				}				
			}else{
				//当没有令牌的时候
				HttpSession httpSession = request.getSession(create);
				session = httpSession == null ? null:getSession(httpSession);				
			}
		}else{			
			//当没有令牌的时候
			HttpSession httpSession = request.getSession(create);
			session = httpSession == null ? null:getSession(httpSession);
		}
		
		return session;
	}
	
	protected String getClientIp(HttpServletRequest request) {
		/**
		 * 支持负载均衡器的X-Forwarded-For
		 */
		String ip = request.getHeader(ForwardedHeader);
		return (ip == null || ip.length() <= 0) ? request.getRemoteHost() : ip;
	}
	
	protected static String ForwardedHeader = "X-Forwarded-For";
	static{
		Settings settings = Settings.get();
		ForwardedHeader = settings.GetValue("http.forwardedheader", ForwardedHeader);
	}	
}
