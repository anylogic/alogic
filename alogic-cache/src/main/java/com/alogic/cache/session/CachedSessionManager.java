package com.alogic.cache.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.w3c.dom.Element;
import com.alogic.cache.context.CacheSource;
import com.alogic.cache.core.CacheStore;
import com.alogic.cache.core.MultiFieldObject;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.Session;
import com.logicbus.backend.SessionManager;
import com.logicbus.backend.server.http.HttpContext;

/**
 * 基于缓存的会话管理器
 * 
 * @author duanyy
 * @version 1.6.4.43 [20160411 duanyy] <br>
 * - DataProvider增加获取原始值接口 <br>
 */
public class CachedSessionManager extends SessionManager{
	protected String cacheId = "sessions";
	protected CacheStore cache = null;
	
	public CachedSessionManager(Properties p){
		cacheId = PropertiesConstants.getString(p,"cache.sessions.id",cacheId);
	}
	
	/**
	 * 获取相关的缓存
	 * @return 缓存
	 */
	protected CacheStore getCacheStore(){
		if (cacheId == null || cacheId.length() <= 0){
			throw new ServantException("core.e1003","The relational cache is not defined");
		}
		
		CacheSource cs = CacheSource.get();
		
		CacheStore store = cs.get(cacheId);
		
		if (store == null){
			throw new ServantException("core.e1003","The cache is not found,cacheId=" + cacheId);
		}
		
		return store;
	}		
	
	@Override
	public Session getSession(Context ctx, boolean create) {
		if (!(ctx instanceof HttpContext)){
			throw new ServantException("core.e1001","The Context is not a HttpContext instance.");
		}
		
		HttpContext httpContext = (HttpContext)ctx;
		HttpServletRequest request = httpContext.getRequest();

		return getSession(request,create);
	}

	@Override
	public Session getSession(HttpServletRequest request, boolean create){
		HttpSession httpSession = request.getSession(create);
		return httpSession == null ? null:getSession(httpSession);
	}
	
	protected Session getSession(HttpSession httpSession) {
		if (cache == null){
			cache = getCacheStore();
		}
		String id = httpSession.getId();
		MultiFieldObject session = cache.get(id, true);
		if (session == null){
			synchronized (this){
				session = cache.get(id, true);
				if (session == null){
					//不存在，创建一个
					CachedSessionObject newObject = new CachedSessionObject(id);
					cache.set(id, newObject);
					session = cache.get(id, true);
				}
			}
		}
		return new CachedSession(session,httpSession);
	}	

	/**
	 * 缓存中对象
	 * @author duanyy
	 *
	 */
	public static class CachedSessionObject implements MultiFieldObject {
		/**
		 * id
		 */
		protected String id;
		
		/**
		 * 上次访问时间
		 */
		protected long lastVisitedTime = System.currentTimeMillis();
		
		/**
		 * 简单的keyvalue
		 */
		protected Map<String,String> stringValues = new HashMap<String,String>(); // NOSONAR
		
		/**
		 * Hash类型的values
		 */
		protected Map<String,Map<String,String>> hashValues = null;
		
		/**
		 * Set类型的values
		 */
		protected Map<String,Set<String>> setValues = null;
		
		protected static Object context = new Object();
		
		public CachedSessionObject(){
			// nothing to do
		}
		
		public CachedSessionObject(String pId){
			id = pId;
		}		
		
		@Override
		public String getId() {
			return id;
		}

		@Override
		public boolean isExpired() {
			return false;
		}

		@Override
		public void expire() {
			stringValues.clear();
			if (hashValues != null){
				hashValues.clear();
			}
			if (setValues != null){
				setValues.clear();
			}
		}

		@Override
		public void toXML(Element e) {
			// 暂不支持
		}

		@Override
		public void fromXML(Element e) {
			// 暂不支持
		}

		@Override
		public void toJson(Map<String, Object> json) {
			if (json == null){
				return ;
			}

			JsonTools.setString(json, "id", id);
			
			if (stringValues != null){
				json.put("strings", stringValues);
			}
			
			if (hashValues != null){
				json.put("hashs", hashValues);
			}
			
			if (setValues != null){
				Map<String,Object> hashs = new HashMap<String,Object>();
				
				Iterator<Entry<String,Set<String>>> iter = setValues.entrySet().iterator();
				
				while (iter.hasNext()){
					Entry<String,Set<String>> entry = iter.next();
					List<Object> values = new ArrayList<Object>();
					
					Iterator<String> iterator = entry.getValue().iterator();
					while (iterator.hasNext()){
						values.add(iterator.next());
					}
					
					hashs.put(entry.getKey(),values);
				}
			}
		}

		@Override
		public void fromJson(Map<String, Object> json) {
			// 暂不支持
		}

		@Override
		public String getValue(String varName, Object context,
				String defaultValue) {
			String found = stringValues.get(varName);
			return found == null || found.length() <= 0 ? defaultValue:found;
		}

		@Override
		public String getRawValue(String varName, Object context, String dftValue) {
			return getValue(varName,context,dftValue);
		}		
		
		@Override
		public Object getContext(String varName) {
			return context;
		}

		@Override
		public void setField(String key, String value) {
			lastVisitedTime = System.currentTimeMillis();
			stringValues.put(key, value);
		}

		@Override
		public String getField(String key, String dftValue) {
			lastVisitedTime = System.currentTimeMillis();
			String found = stringValues.get(key);
			return found == null || found.length() <= 0 ? dftValue:found;
		}
		

		@Override
		public String[] keys() {
			return stringValues.keySet().toArray(new String[stringValues.size()]);
		}

		@Override
		public int count() {
			return stringValues.size();
		}
				
		@Override
		public String hGet(String id, String field, String dftValue) {
			lastVisitedTime = System.currentTimeMillis();
			if (hashValues == null) 
				return dftValue;
			Map<String,String> found = hashValues.get(id);
			if (found == null){
				return dftValue;
			}
			return found.get(field);
		}

		@Override
		public void hSet(String id, String field, String value) {
			lastVisitedTime = System.currentTimeMillis();
			synchronized (context){
				if (hashValues == null){
					hashValues = new HashMap<String,Map<String,String>>();
				}
				
				Map<String,String> found = hashValues.get(id);
				if (found == null){
					found = new HashMap<String,String>();
					hashValues.put(id, found);
				}
				
				found.put(field, value);
			}
		}

		@Override
		public boolean hExist(String id, String field) {
			lastVisitedTime = System.currentTimeMillis();
			if (hashValues == null) 
				return false;
			Map<String,String> found = hashValues.get(id);
			if (found == null){
				return false;
			}
			return found.containsKey(field);
		}

		@Override
		public Map<String, String> hGetAll(String id) {
			lastVisitedTime = System.currentTimeMillis();
			if (hashValues == null) 
				return null;
			return hashValues.get(id);
		}

		@Override
		public int hLen(String id) {
			lastVisitedTime = System.currentTimeMillis();
			if (hashValues == null) 
				return 0;
			Map<String,String> found = hashValues.get(id);
			if (found == null){
				return 0;
			}
			return found.size();
		}

		@Override
		public String[] hKeys(String id) {
			lastVisitedTime = System.currentTimeMillis();
			if (hashValues == null) 
				return null;
			Map<String,String> found = hashValues.get(id);
			if (found == null){
				return null;
			}
			return found.keySet().toArray(new String[found.size()]);
		}

		@Override
		public String[] hValues(String id) {
			lastVisitedTime = System.currentTimeMillis();
			if (hashValues == null) 
				return null;
			Map<String,String> found = hashValues.get(id);
			if (found == null){
				return null;
			}
			return found.values().toArray(new String[found.size()]);
		}

		@Override
		public void sAdd(String id, String... member) {
			lastVisitedTime = System.currentTimeMillis();
			synchronized (context){
				if (setValues == null){
					setValues = new HashMap<String,Set<String>>();
				}
				
				Set<String> found = setValues.get(id);
				if (found == null){
					found = new HashSet<String>();
					setValues.put(id, found);
				}
				
				for (String m:member){
					found.add(m);
				}
			}
		}

		@Override
		public void sDel(String id, String... member) {
			lastVisitedTime = System.currentTimeMillis();
			synchronized (context){
				if (setValues == null){
					setValues = new HashMap<String,Set<String>>();
				}
				
				Set<String> found = setValues.get(id);
				if (found != null){
					for (String m:member){
						found.remove(m);
					}
				}
			}
		}

		@Override
		public int sSize(String id) {
			lastVisitedTime = System.currentTimeMillis();
			if (setValues == null){
				return 0;
			}
			
			Set<String> found = setValues.get(id);
			return found == null ? 0 : found.size();
		}

		@Override
		public String[] sMembers(String id) {
			lastVisitedTime = System.currentTimeMillis();
			if (setValues == null){
				return null;
			}
			
			Set<String> found = setValues.get(id);
			if (found == null){
				return null;
			}
			
			return found.toArray(new String[found.size()]);
		}
		
		@Override
		public boolean sExist(String id, String member) {
			lastVisitedTime = System.currentTimeMillis();
			if (setValues == null){
				return false;
			}
			
			Set<String> found = setValues.get(id);
			if (found == null){
				return false;
			}
			return found.contains(member);
		}		

		@Override
		public void del(String id) {
			stringValues.remove(id);
		}

		@Override
		public void copyTo(MultiFieldObject another) {
			if (another != null){
				if (stringValues != null){
					Iterator<Entry<String,String>> iter = stringValues.entrySet().iterator();
					
					while (iter.hasNext()){
						Entry<String,String> keyvalue = iter.next();
						another.setField(keyvalue.getKey(), keyvalue.getValue());
					}				
				}
				if (hashValues != null){
					Iterator<Entry<String,Map<String,String>>> iter = hashValues.entrySet().iterator();
					
					while (iter.hasNext()){
						Entry<String,Map<String,String>> entry = iter.next();
						
						String id = entry.getKey();
						Map<String,String> values = entry.getValue();
						
						Iterator<Entry<String,String>> iterator = values.entrySet().iterator();
						while (iterator.hasNext()){
							Entry<String,String> value = iterator.next();
							another.hSet(id, value.getKey(), value.getValue());
						}
					}
				}
				if (setValues != null){
					Iterator<Entry<String,Set<String>>> iter = setValues.entrySet().iterator();
					
					while (iter.hasNext()){
						Entry<String,Set<String>> entry = iter.next();
						String id = entry.getKey();
						Set<String> values = entry.getValue();
						another.sAdd(id, values.toArray(new String[values.size()]));
					}
				}
			}
		}

		@Override
		public long getLastVisitedTime() {
			return lastVisitedTime;
		}
	}
	
	public static class CachedSession extends Session{
		protected MultiFieldObject agent;
		protected HttpSession session;
		public CachedSession(MultiFieldObject object,HttpSession httpSession){
			agent = object;
			session = httpSession;
		}
		
		@Override
		public String hGet(String id, String field, String dftValue) {
			return agent.hGet(id, field, dftValue);
		}

		@Override
		public void hSet(String id, String field, String value) {
			agent.hSet(id, field, value);
		}

		@Override
		public boolean hExist(String id, String field) {
			return agent.hExist(id, field);
		}

		@Override
		public Map<String, String> hGetAll(String id) {
			return agent.hGetAll(id);
		}

		@Override
		public int hLen(String id) {
			return agent.hLen(id);
		}

		@Override
		public String[] hKeys(String id) {
			return agent.hKeys(id);
		}

		@Override
		public String[] hValues(String id) {
			return agent.hValues(id);
		}

		@Override
		public void sAdd(String id, String... member) {
			agent.sAdd(id, member);
		}

		@Override
		public void sDel(String id, String... member) {
			agent.sDel(id, member);
		}

		@Override
		public int sSize(String id) {
			return agent.sSize(id);
		}

		@Override
		public String[] sMembers(String id) {
			return agent.sMembers(id);
		}

		@Override
		public boolean sExist(String id,String member){
			return agent.sExist(id, member);
		}
		
		@Override
		public long getCreateTime() {
			return agent.getLastVisitedTime();
		}

		@Override
		public void invalidate() {
			session.invalidate();
			agent.expire();
		}

		@Override
		public String getId() {
			return agent.getId();
		}

		@Override
		public void del(String id) {
			agent.del(id);
		}

		@Override
		protected void _SetValue(String _name, String _value) {
			agent.setField(_name,_value);
		}

		@Override
		protected String _GetValue(String _name) {
			return agent.getField(_name,"");
		}

		@Override
		public void Clear() {
			agent.expire();
		}
		
	}
}
