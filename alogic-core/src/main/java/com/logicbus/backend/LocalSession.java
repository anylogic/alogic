package com.logicbus.backend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;


/**
 * 本地实现的Session
 * 
 * @author duanyy
 * @version 1.6.3.10 [20150401 duanyy] <br>
 * - 修正Session值不存在时的空指针问题。 <br>
 * 
 */
public class LocalSession extends Session {
	private HttpSession httpSession = null;
	
	public LocalSession(HttpSession _httpSession){
		httpSession = _httpSession;
	}
	
	@Override
	public String hGet(String id, String field, String dftValue) {
		Object found = httpSession.getAttribute(id);
		if (found != null && found instanceof Map){
			@SuppressWarnings("unchecked")
			Map<String,String> map = (Map<String,String>) found;
			String value = map.get(field);
			return value == null || value.length() <= 0 ? dftValue : value;
		}else{
			return dftValue;
		}
	}

	@Override
	public void hSet(String id, String field, String value) {
		synchronized (httpSession){
			Object found = httpSession.getAttribute(id);
			if (found == null && ! (found instanceof Set)){
				Map<String,String> map = new HashMap<String,String>();
				map.put(field, value);
				httpSession.setAttribute(id, map);
			}else{
				@SuppressWarnings("unchecked")
				Map<String,String> map = (Map<String,String>) found;
				map.put(field, value);
			}
		}
	}

	@Override
	public boolean hExist(String id, String field) {
		Object found = httpSession.getAttribute(id);
		if (found != null && found instanceof Map){
			@SuppressWarnings("unchecked")
			Map<String,String> map = (Map<String,String>) found;
			return map.containsKey(field);
		}else{
			return false;
		}
	}

	@Override
	public Map<String,String> hGetAll(String id) {
		Object found = httpSession.getAttribute(id);
		if (found != null && found instanceof Map){
			@SuppressWarnings("unchecked")
			Map<String,String> map = (Map<String,String>) found;
			return map;
		}else{
			return null;
		}
	}

	@Override
	public int hLen(String id) {
		Object found = httpSession.getAttribute(id);
		if (found != null && found instanceof Map){
			@SuppressWarnings("unchecked")
			Map<String,String> map = (Map<String,String>) found;
			return map.size();
		}else{
			return 0;
		}
	}

	@Override
	public String[] hKeys(String id) {
		Object found = httpSession.getAttribute(id);
		if (found != null && found instanceof Map){
			@SuppressWarnings("unchecked")
			Map<String,String> map = (Map<String,String>) found;
			return map.keySet().toArray(new String[map.size()]);
		}else{
			return null;
		}
	}

	@Override
	public String[] hValues(String id) {
		Object found = httpSession.getAttribute(id);
		if (found != null && found instanceof Map){
			@SuppressWarnings("unchecked")
			Map<String,String> map = (Map<String,String>) found;
			return map.values().toArray(new String[map.size()]);
		}else{
			return null;
		}
	}

	@Override
	public void sAdd(String id, String... member) {
		synchronized (httpSession){
			Object found = httpSession.getAttribute(id);
			if (found == null && ! (found instanceof Set)){
				Set<String> set = new HashSet<String>();
				
				for (String m:member){
					set.add(m);
				}
				
				httpSession.setAttribute(id, set);
			}else{
				@SuppressWarnings("unchecked")
				Set<String> set = (Set<String>)found;
				
				for (String m:member){
					set.add(m);
				}
			}
		}
	}

	@Override
	public void sDel(String id, String... member) {
		synchronized (httpSession){
			Object found = httpSession.getAttribute(id);
			if (found != null && found instanceof Set){
				@SuppressWarnings("unchecked")
				Set<String> set = (Set<String>)found;
				
				for (String m:member){
					set.remove(m);
				}
			}
		}
	}

	@Override
	public int sSize(String id) {
		Object found = httpSession.getAttribute(id);
		if (found != null && found instanceof Set){
			@SuppressWarnings("unchecked")
			Set<String> set = (Set<String>)found;
			return set.size();
		}else{
			return 0;
		}
	}

	@Override
	public String[] sMembers(String id) {
		Object found = httpSession.getAttribute(id);
		if (found != null && found instanceof Set){
			@SuppressWarnings("unchecked")
			Set<String> set = (Set<String>)found;
			return set.toArray(new String[set.size()]);
		}else{
			return null;
		}
	}

	@Override
	protected void _SetValue(String _name, String _value) {
		httpSession.setAttribute(_name, _value);
	}

	@Override
	protected String _GetValue(String _name) {
		Object value = httpSession.getAttribute(_name);
		return value == null ? null : value.toString();
	}

	@Override
	public void Clear() {
		httpSession.invalidate();
	}

	@Override
	public long getCreateTime() {
		return httpSession.getCreationTime();
	}

	@Override
	public void invalidate() {
		httpSession.invalidate();
	}
	
	@Override
	public String getId(){
		return httpSession.getId(); 
	}

	@Override
	public void del(String id) {
		httpSession.removeAttribute(id);
	}
}
