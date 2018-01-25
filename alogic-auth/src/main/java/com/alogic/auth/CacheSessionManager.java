package com.alogic.auth;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.w3c.dom.Element;

import com.alogic.cache.CacheObject;
import com.alogic.cache.naming.CacheStoreFactory;
import com.alogic.load.Store;
import com.anysoft.util.BaseException;
import com.anysoft.util.Pair;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 基于Cache框架的会话管理器
 * 
 * @author yyduan
 * @since 1.6.11.13 
 * 
 */
public class CacheSessionManager extends SessionManager.Abstract{
	/**
	 * CacheId
	 */
	protected String cacheId = "session";
	
	/**
	 * Cache实例
	 */
	protected Store<CacheObject> store = null;
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		cacheId = PropertiesConstants.getString(p,"cacheId", cacheId);
		
		store = CacheStoreFactory.get(cacheId);
		if (store == null){
			throw new BaseException("core.e1003","The cache is not found,cacheId=" + cacheId);
		}
	}
	
	@Override
	public Session getSession(String sessionId, boolean create) {
		CacheObject session = store.load(sessionId, true);
		if (session == null && create){
			session = store.newObject(sessionId);
			store.save(sessionId, session, true);
		}
		
		return session == null ? null : new CacheSession(session);
	}

	@Override
	public void delSession(String sessionId) {
		store.del(sessionId);
	}

	/**
	 * 基于CacheObject的Session实现
	 * 
	 * @author yyduan
	 *
	 */
	public static class CacheSession implements Session{
		protected CacheObject cacheObject = null;
		
		public CacheSession(CacheObject cache){
			this.cacheObject = cache;
		}
				
		@Override
		public void sAdd(String group, String... members) {
			this.cacheObject.sAdd(group, members);
		}

		@Override
		public void sDel(String group, String... members) {
			this.cacheObject.sDel(group,members);
		}

		@Override
		public void sDel(String group) {
			this.cacheObject.sDel(group);
		}

		@Override
		public int sSize(String group) {
			return this.cacheObject.sSize(group);
		}

		@Override
		public List<String> sMembers(String group, String condition) {
			return this.cacheObject.sMembers(group, condition);
		}

		@Override
		public boolean sExist(String group, String member) {
			return this.cacheObject.sExist(group, member);
		}

		@Override
		public String getId() {
			return this.cacheObject.getId();
		}

		@Override
		public long getTimestamp() {
			return this.cacheObject.getTimestamp();
		}

		@Override
		public boolean isExpired() {
			return this.cacheObject.isExpired();
		}

		@Override
		public void expire() {
			this.cacheObject.expire();
		}

		@Override
		public void report(Element xml) {
			this.cacheObject.report(xml);
		}

		@Override
		public void report(Map<String, Object> json) {
			this.cacheObject.report(json);
		}

		@Override
		public String getValue(String varName, Object context,
				String defaultValue) {
			return this.cacheObject.getValue(varName, context, defaultValue);
		}

		@Override
		public String getRawValue(String varName, Object context,
				String dftValue) {
			return this.cacheObject.getRawValue(varName, context, dftValue);
		}

		@Override
		public Object getContext(String varName) {
			return this.cacheObject.getContext(varName);
		}

		@Override
		public void hSet(String group, String key, String value,
				boolean overwrite) {
			this.cacheObject.hSet(group, key, value, overwrite);
		}

		@Override
		public String hGet(String group, String key, String dftValue) {
			return this.cacheObject.hGet(group, key, dftValue);
		}

		@Override
		public boolean hExist(String group, String key) {
			return this.cacheObject.hExist(group, key);
		}

		@Override
		public List<Pair<String, String>> hGetAll(String group, String condition) {
			return this.cacheObject.hGetAll(group, condition);
		}

		@Override
		public int hLen(String group) {
			return this.cacheObject.hLen(group);
		}

		@Override
		public List<String> hKeys(String group, String condition) {
			return this.cacheObject.hKeys(group, condition);
		}

		@Override
		public void hDel(String group, String key) {
			this.cacheObject.hDel(group,key);
		}

		@Override
		public void hDel(String group) {
			this.cacheObject.hDel(group);
		}

		@Override
		public boolean isLoggedIn() {
			return BooleanUtils.toBoolean(hGet(DEFAULT_GROUP,LOGIN_KEY, "false"));
		}
		
		@Override
		public void setLoggedIn(boolean loggedIn){
			this.hSet(DEFAULT_GROUP,LOGIN_KEY, BooleanUtils.toStringTrueFalse(loggedIn), true);
		}
		
	}
}
