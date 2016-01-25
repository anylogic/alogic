package com.alogic.cache.session;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.alogic.cache.context.CacheSource;
import com.alogic.cache.core.CacheStore;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.logicbus.backend.ServantException;

/**
 * 会话信息清理工具
 * 
 * @author duanyy
 *
 */
public class SessionCleaner implements HttpSessionListener {
	protected String cacheId = "sessions";
	public SessionCleaner(){
		cacheId = PropertiesConstants.getString(Settings.get(),"cache.sessions.id",cacheId);
	}
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		// nothing to do
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		CacheStore cache = getCacheStore();
		cache.expire(session.getId());
	}
	
	/**
	 * 获取相关的缓存
	 * @return 缓存
	 */
	protected CacheStore getCacheStore(){
		if (cacheId == null || cacheId.length() <= 0){
			throw new ServantException("core.cache_not_defined","The relational cache is not defined");
		}
		
		CacheSource cs = CacheSource.get();
		
		CacheStore store = cs.get(cacheId);
		
		if (store == null){
			throw new ServantException("core.cache_not_found","The cache is not found,cacheId=" + cacheId);
		}
		
		return store;
	}	

}
