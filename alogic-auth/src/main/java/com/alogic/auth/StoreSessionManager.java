package com.alogic.auth;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Element;

import com.alogic.load.Store;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 基于Store框架的SessionManager 
 * 
 * @author Administrator
 *
 */
public class StoreSessionManager extends SessionManager.Abstract{
	
	/**
	 * Store
	 */
	protected Store<Session> store = null;
	
	@Override
	public void configure(Element e, Properties p) {
		XmlElementProperties props = new XmlElementProperties(e,p);
		
		Factory<Store<Session>> f = new Factory<Store<Session>>();
		try {
			store = f.newInstance(e, props, "store",Session.LocalCacheStore.class.getName());
		}catch (Exception ex){
			LOG.error("Can not create store :" + XmlTools.node2String(e));
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}
		
		configure(props);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		if (store == null){
			store = new Session.LocalCacheStore();
			store.configure(p);
		}
	}
	
	@Override
	public Session getSession(String sessionId, boolean create) {
		Session sess = store.load(sessionId, true);
		if (sess == null && create){
			synchronized(this){
				sess = store.load(sessionId, true);
				if (sess == null){
					sess = new LocalSession(sessionId,this);
					store.save(sessionId, sess,true);
				}
			}
		}
		return sess;
	}

	@Override
	public void delSession(String sessionId) {
		store.del(sessionId);
	}

}
