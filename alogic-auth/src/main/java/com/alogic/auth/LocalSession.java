package com.alogic.auth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Pair;
import com.anysoft.util.StringMatcher;
import com.anysoft.util.XmlTools;

/**
 * 本地服务的Session
 * 
 * <p>
 * LocalSession会在HttpSession中构造一个HashMap对象和一个HashSet对象，用于实现HashObject和SetObject接口。
 * 
 * @author duanyy
 * @since 1.6.10.10
 */
public class LocalSession implements Session{
		
	/**
	 * HttpSession代理
	 */
	private HttpSession httpSession = null;
	
	/**
	 * 通过一个HttpSession来构造
	 * @param session HttpSession
	 */
	public LocalSession(HttpSession session){
		this.httpSession = session;
	}
	
	@Override
	public boolean isLoggedIn() {
		return BooleanUtils.toBoolean(hGet(DEFAULT_GROUP,LOGIN_KEY, "false"));
	}
	
	@Override
	public void setLoggedIn(boolean loggedIn){
		this.hSet(DEFAULT_GROUP,LOGIN_KEY, BooleanUtils.toStringTrueFalse(loggedIn), true);
	}
	
	@Override
	public void setAttribute(String name, Object value) {
		this.httpSession.setAttribute(name, value);
	}

	@Override
	public Object getAttribute(String name) {
		return this.httpSession.getAttribute(name);
	}
	
	/**
	 * 获取内置的setObject
	 * 
	 * <p>
	 * 用于获取内置的setObject, 当该对象不存在时，如果create为true，则创建一个。
	 * 
	 * @param group 分组
	 * @param create 当不存在时是否创建
	 * @return 内置的setObject
	 */
	@SuppressWarnings("unchecked")
	protected Set<String> getSetObject(String group,boolean create){
		Object setObject = httpSession.getAttribute(group);
		if (setObject == null && create){
			synchronized(this){
				setObject = httpSession.getAttribute(group);
				if (setObject == null){
					setObject = Collections.newSetFromMap(new ConcurrentHashMap<String,Boolean>());
					httpSession.setAttribute(group, setObject);
				}
			}
		}
		return (Set<String>) setObject;
	}
	
	/**
	 * 获取内置的mapObject
	 * 
	 * <p>
	 * 用于获取内置的mapObject,当该对象不存在时，如果create为true，则创建一个。
	 * 
	 * @param group 分组
	 * @param create 当不存在时是否创建
	 * @return 内置的mapObject
	 */
	@SuppressWarnings("unchecked")
	protected  Map<String,String> getMapObject(String group,boolean create){
		Object mapObject = httpSession.getAttribute(group);
		if (mapObject == null){
			synchronized(this){
				mapObject = httpSession.getAttribute(group);
				if (mapObject == null){
					mapObject = new ConcurrentHashMap<String,String>();
					httpSession.setAttribute(group, mapObject);
				}
			}
		}
		return (Map<String,String>) mapObject;
	}
	
	
	@Override
	public void sAdd(String group,String... members) {
		Set<String> obj = getSetObject(group,true);
		for (String m:members){
			obj.add(m);
		}
	}

	@Override
	public void sDel(String group,String... members) {
		Set<String> obj = getSetObject(group,false);
		if (obj != null){
			for (String m:members){
				obj.remove(m);
			}
		}
	}

	@Override
	public int sSize(String group) {
		Set<String> obj = getSetObject(group,false);
		return obj == null ? 0 : obj.size();
	}
	
	private boolean isConditionValid(String condition){
		return StringUtils.isNotEmpty(condition) && !condition.equals("*");
	}

	@Override
	public List<String> sMembers(String group,String condition) {
		List<String> result = new ArrayList<String>();
		Set<String> obj = getSetObject(group,false);
		if (obj != null){
			if (isConditionValid(condition)){
				StringMatcher matcher = new StringMatcher(condition);			
				Iterator<String> iter = obj.iterator();			
				while (iter.hasNext()){
					String member = iter.next();				
					if (matcher.match(member)){
						result.add(member);
					}
				}
			}else{
				Iterator<String> iter = obj.iterator();			
				while (iter.hasNext()){
					result.add(iter.next());
				}
			}
		}
		return result;
	}

	@Override
	public boolean sExist(String group,String member) {
		Set<String> obj = getSetObject(group,false);
		return obj != null ? obj.contains(member) : false;
	}

	@Override
	public String getId() {
		return httpSession.getId();
	}

	@Override
	public long getTimestamp() {
		return httpSession.getLastAccessedTime();
	}

	@Override
	public boolean isExpired() {
		return false;
	}

	@Override
	public void expire() {
		httpSession.invalidate();
	}

	@Override
	public void report(Element xml) {
		if (xml != null){
			XmlTools.setString(xml,"module",getClass().getName());
			XmlTools.setString(xml, "id", getId());
			XmlTools.setString(xml,"timestamp", String.valueOf(getTimestamp()));
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json, "module", getClass().getName());
			JsonTools.setString(json, "id", getId());
			JsonTools.setLong(json, "timestamp", getTimestamp());
		}
	}

	@Override
	public String getValue(String varName, Object context, String defaultValue) {
		return getRawValue(varName,context,defaultValue);
	}

	@Override
	public String getRawValue(String varName, Object context, String dftValue) {
		@SuppressWarnings("unchecked")
		Map<String,String> mapObject = context != null ?
				(Map<String,String>)context
				:getMapObject(DEFAULT_GROUP,false);
				
		if (mapObject != null){
			String value = mapObject.get(varName);
			return StringUtils.isEmpty(value)?dftValue:value;
		}else{
			return dftValue;
		}
	}

	@Override
	public Object getContext(String varName) {
		return getMapObject(DEFAULT_GROUP,false);
	}

	@Override
	public void hSet(String group,String key, String value, boolean overwrite) {
		Map<String,String> mapObject = getMapObject(group,true);
		if (mapObject.containsKey(key)){
			if (overwrite){
				mapObject.put(key, value);
			}
		}else{
			mapObject.put(key, value);
		}
	}

	@Override
	public String hGet(String group,String key, String dftValue) {
		Map<String,String> mapObject = getMapObject(group,false);
		
		if (mapObject != null){
			String value = mapObject.get(key);
			return StringUtils.isEmpty(value)?dftValue:value;
		}else{
			return dftValue;
		}
	}

	@Override
	public boolean hExist(String group,String key) {
		Map<String,String> mapObject = getMapObject(group,false);
		
		return mapObject == null ? false:mapObject.containsKey(key);
	}

	@Override
	public List<Pair<String, String>> hGetAll(String group,String condition) {
		List<Pair<String,String>> result = new ArrayList<Pair<String,String>>();
		Map<String,String> mapObject = getMapObject(group,false);
		
		if (mapObject != null){
			if (isConditionValid(condition)){
				StringMatcher matcher = new StringMatcher(condition);
				Iterator<Entry<String,String>> iter = mapObject.entrySet().iterator();		
				while (iter.hasNext()){
					Entry<String,String> entry = iter.next();
					if (matcher.match(entry.getKey())){
						result.add(new Pair.Default<String, String>(entry.getKey(), entry.getValue()));
					}
				}
			}else{
				Iterator<Entry<String,String>> iter = mapObject.entrySet().iterator();		
				while (iter.hasNext()){
					Entry<String,String> entry = iter.next();
					result.add(new Pair.Default<String, String>(entry.getKey(), entry.getValue()));
				}
			}
		}
		return result;
	}

	@Override
	public int hLen(String group) {
		Map<String,String> mapObject = getMapObject(group,false);
		return mapObject == null ? 0 : mapObject.size();
	}

	@Override
	public List<String> hKeys(String group,String condition) {
		List<String> result = new ArrayList<String>();
		Map<String,String> mapObject = getMapObject(group,false);
		
		if (mapObject != null){
			if (isConditionValid(condition)){
				StringMatcher matcher = new StringMatcher(condition);
				Iterator<String> iter = mapObject.keySet().iterator();
				while (iter.hasNext()){
					String key = iter.next();
					if (matcher.match(key)){
						result.add(key);
					}
				}
			}else{
				Iterator<String> iter = mapObject.keySet().iterator();
				while (iter.hasNext()){
					result.add(iter.next());
				}
			}
		}
		return result;
	}

	@Override
	public void hDel(String group,String key) {
		Map<String,String> mapObject = getMapObject(group,false);
		if (mapObject != null){
			mapObject.remove(key);
		}
	}
}
