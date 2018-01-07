package com.alogic.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 利用LocalSession实现的SessionManager
 * 
 * @author duanyy
 * @since 1.6.10.10
 * 
 * @version 1.6.11.7 [20180107 duanyy] <br>
 * - 优化Session管理 <br>
 */
public class LocalSessionManager extends SessionManager.Abstract{
	
	/**
	 * Sessions
	 */
	protected Map<String,Session> sessions = new ConcurrentHashMap<String,Session>();

	@Override
	public Session getSession(String sessionId, boolean create) {
		Session sess = sessions.get(sessionId);
		if (sess == null && create){
			synchronized(this){
				sess = sessions.get(sessionId);
				if (sess == null){
					sess = new LocalSession(sessionId,this);
					sessions.put(sessionId, sess);
				}
			}
		}
		return sess;
	}

	@Override
	public void delSession(String sessionId) {
		sessions.remove(sessionId);
	}
	
}
