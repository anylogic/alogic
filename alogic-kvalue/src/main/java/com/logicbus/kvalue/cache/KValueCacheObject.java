package com.logicbus.kvalue.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.cache.CacheObject;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Pair;
import com.anysoft.util.StringMatcher;
import com.anysoft.util.XmlTools;
import com.logicbus.kvalue.core.HashRow;
import com.logicbus.kvalue.core.SetRow;

/**
 * 基于KValue的CacheObject
 * 
 * @author yyduan
 * @since 1.6.11.13
 * 
 * @version 1.6.11.20 [20180223 duanyy] <br>
 * - 缓存实现的间隔符由$更改为# <br>
 * 
 */
public class KValueCacheObject implements CacheObject{
	/**
	 * 对象id
	 */
	protected String id;
	
	/**
	 * HashRow
	 */
	protected HashRow hash = null;
	
	/**
	 * SetRow
	 */
	protected SetRow set = null;
	
	/**
	 * 分隔符
	 */
	protected static final char SEPERATOR = '#';
	
	/**
	 * 生命周期
	 */
	protected long ttl = 30 * 60 * 1000L;
	
	/**
	 * 上次访问时间
	 */
	protected long lastVisitedTime = 0;
	
	public KValueCacheObject(String id,HashRow hash,SetRow set,long ttl){
		this.id = id;
		this.hash = hash;
		this.set = set;
		this.ttl = ttl;
	}
	
	/**
	 * 标记已经访问过一次
	 */
	protected void visited(){
		long now = System.currentTimeMillis();
		if (now - lastVisitedTime > 60 * 1000){
			//上次写入大于一分钟，才写缓存，避免多次调用
			lastVisitedTime = now;
			hash.set("t", lastVisitedTime);
			hash.ttl(ttl,TimeUnit.MILLISECONDS);				
			set.ttl(ttl, TimeUnit.MILLISECONDS);
		}		
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public boolean isValid() {
		return StringUtils.isNotEmpty(getId()) && !isExpired();
	}

	@Override
	public long getTimestamp() {
		return hash == null ? 0 : hash.get("t", 0);
	}

	@Override
	public boolean isExpired() {
		if (lastVisitedTime > 0){
			return false;
		}
		lastVisitedTime = hash == null ? 0 : hash.get("t", 0);
		return lastVisitedTime <= 0;
	}

	@Override
	public void expire() {
		hash.delete();
		set.delete();
	}
	
	@Override
	public void hSet(String group, String key, String value, boolean overwrite) {
		visited();
		hash.set(Tool.getKey(group, key), value);
	}

	@Override
	public String hGet(String group, String key, String dftValue) {
		visited();
		String value = hash.get(Tool.getKey(group, key), dftValue);
		return value == null ? dftValue : value;
	}

	@Override
	public boolean hExist(String group, String key) {
		visited();
		return hash.exists(Tool.getKey(group, key));
	}

	@Override
	public List<Pair<String, String>> hGetAll(String group, String condition) {
		visited();
		Map<String,String> all = hash.getAll();
		List<Pair<String,String>> filtered = new ArrayList<Pair<String,String>>();

		if (Tool.isConditionValid(condition)){
			StringMatcher matcher = new StringMatcher(condition);
			Iterator<Entry<String,String>> iterator = all.entrySet().iterator();
			while (iterator.hasNext()){
				Entry<String,String> entry = iterator.next();
				
				String key = entry.getKey();
				if (Tool.isField(key,group)){
					String field = Tool.getField(key);
					if (matcher.match(field)){
						filtered.add(new Pair.Default<String, String>(field, entry.getValue()));
					}
				}
			}
		}else{
			Iterator<Entry<String,String>> iterator = all.entrySet().iterator();
			while (iterator.hasNext()){
				Entry<String,String> entry = iterator.next();
				
				String key = entry.getKey();
				if (Tool.isField(key,group)){
					String field = Tool.getField(key);
					filtered.add(new Pair.Default<String, String>(field, entry.getValue()));
				}
			}			
		}
		return filtered;
	}

	@Override
	public int hLen(String group) {
		visited();
		Map<String,String> all = hash.getAll();
	
		Iterator<Entry<String,String>> iterator = all.entrySet().iterator();

		int count = 0;
		while (iterator.hasNext()){
			Entry<String,String> entry = iterator.next();
			
			String key = entry.getKey();
			if (Tool.isField(key,group)){
				count ++;
			}
		}
		
		return count;
	}

	@Override
	public List<String> hKeys(String group, String condition) {
		visited();
		List<String> all = hash.keys();
		List<String> keys = new ArrayList<String>();
		
		for (String key:all){
			if (Tool.isField(key,group)){
				keys.add(Tool.getField(key));
			}				
		}
		
		return keys;
	}

	@Override
	public void hDel(String group, String key) {
		visited();
		hash.del(Tool.getKey(group, key));
	}

	@Override
	public void hDel(String group) {
		visited();
		List<String> all = hash.keys();
		for (String key:all){
			if (Tool.isField(key,group)){
				hash.del(Tool.getField(key));
			}				
		}
	}

	@Override
	public void report(Element xml) {
		if (xml != null){
			XmlTools.setString(xml, "module", getClass().getName());
			XmlTools.setString(xml, "id", getId());
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json, "module", getClass().getName());
			JsonTools.setString(json, "id", getId());
			toJson(json);
		}
	}

	@Override
	public void sAdd(String group, String... members) {
		visited();
		for (String m:members){
			set.add(Tool.getKey(group, m));
		}
	}

	@Override
	public void sDel(String group, String... members) {
		visited();
		for (String m:members){
			set.remove(Tool.getKey(group, m));
		}
	}

	@Override
	public void sDel(String group) {
		visited();
		List<String> all = set.getAll();
		for (String m:all){
			if (Tool.isField(m,group)){
				set.remove(Tool.getField(m));
			}
		}
	}

	@Override
	public int sSize(String group) {
		visited();
		List<String> all = set.getAll();		
		int count = 0;		
		for (String m:all){
			if (Tool.isField(m,group)){
				count ++;
			}
		}
		return count;
	}

	@Override
	public List<String> sMembers(String group, String condition) {
		visited();
		List<String> all = set.getAll();
		List<String> members = new ArrayList<String>();

		if (Tool.isConditionValid(condition)){
			StringMatcher matcher = new StringMatcher(condition);
			for (String m:all){
				if (Tool.isField(m,group)){
					String field = Tool.getField(m);
					if (matcher.match(field)){
						members.add(field);
					}
				}
			}
		}else{
			for (String m:all){
				if (Tool.isField(m,group)){
					members.add(Tool.getField(m));
				}
			}
		}
		return members;
	}

	@Override
	public boolean sExist(String group, String member) {
		visited();
		return set.contain(Tool.getKey(group, member));
	}

	@Override
	public String getValue(String varName, Object context, String defaultValue) {
		visited();
		String value = hash.get(Tool.getKey("",varName), defaultValue);
		return value == null ? defaultValue : value;
	}

	@Override
	public String getRawValue(String varName, Object context, String dftValue) {
		return getValue(varName,context,dftValue);
	}

	@Override
	public Object getContext(String varName) {
		return this;
	}

	@Override
	public void toJson(Map<String, Object> json) {
		if (json != null){
			if (hash != null){
				Map<String,String> all = hash.getAll();
				
				Iterator<Entry<String,String>> iterator = all.entrySet().iterator();
				
				Map<String,Map<String,Object>> hashes = new HashMap<String,Map<String,Object>>();
				while (iterator.hasNext()){
					Entry<String,String> entry = iterator.next();
					String key = entry.getKey();					
					if (Tool.isInnerField(key)){
						continue;
					}
					
					String group = Tool.getGroup(key);
					String field = Tool.getField(key);
					String value = entry.getValue();
					if (StringUtils.isEmpty(group)){
						json.put(field, value);
						continue;
					}
					
					Map<String,Object> currentHash = hashes.get(group);
					if (currentHash == null){
						currentHash = new HashMap<String,Object>();
						hashes.put(group,currentHash);
					}
					currentHash.put(field, value);
				}				
				
				if (!hashes.isEmpty()){
					json.put("hash", hashes);
				}
			}
			if (set != null){
				List<String> all = set.getAll();
				Map<String,List<String>> sets = new HashMap<String,List<String>>();
				for (String key:all){
					String group = Tool.getGroup(key);
					String value = Tool.getField(key);
					
					List<String> currentSet = sets.get(group);
					if (currentSet == null){
						currentSet = new ArrayList<String>();
						sets.put(group, currentSet);
					}
					currentSet.add(value);		
				}
				
				if (!sets.isEmpty()){
					json.put("set", sets);
				}
			}
		}
	}

	@Override
	public void fromJson(Map<String, Object> json) {
		// nothing to do
	}

	@Override
	public void copyTo(CacheObject another) {
		if (another != null){
			if (hash != null){
				Map<String,String> all = hash.getAll();
				
				Iterator<Entry<String,String>> iter = all.entrySet().iterator();
				
				while (iter.hasNext()){
					Entry<String,String> entry = iter.next();					
					String group = Tool.getGroup(entry.getKey());
					String field = Tool.getField(entry.getKey());
					
					another.hSet(group, field, entry.getValue(),true);
				}
			}
			if (set != null){
				List<String> all = set.getAll();
				for (String member:all){
					String group = Tool.getGroup(member);
					String value = Tool.getField(member);
					another.sAdd(group, value);					
				}
			}
		}
	}

	/**
	 * 工具类
	 * @author yyduan
	 *
	 */
	public static class Tool {
		
		/**
		 * 查询条件是否有效
		 * @param condition 查询条件
		 * @return 是否有效
		 */
		public static boolean isConditionValid(String condition){
			return StringUtils.isNotEmpty(condition) && !condition.equals("*");
		}
		
		/**
		 * 根据group和field生成组合key
		 * @param group group
		 * @param field field
		 * @return 组合key
		 */
		public static String getKey(String group,String field){
			return group + SEPERATOR + field;
		}
		
		/**
		 * 从一个组合key中获取group
		 * @param key 组合key
		 * @return group
		 */
		public static String getGroup(String key){
			int idx = key.indexOf(SEPERATOR);
			if (idx < 0){
				return "";
			}
			return key.substring(0,idx);			
		}
		
		/**
		 * 从一个组合key中获取field
		 * @param key 组合key
		 * @return
		 */
		public static String getField(String key){
			int idx = key.indexOf(SEPERATOR);
			if (idx < 0){
				return key;
			}
			return key.substring(idx + 1);
		}	
		
		/**
		 * 指定的组合key是否指定group的field
		 * @param key 组合可以
		 * @param group 指定的group
		 * @return 是否是指定group的field
		 */
		public static boolean isField(String key,String group){
			return key.startsWith(group + SEPERATOR);
		}
		
		/**
		 * 指定的组合key是否内部的field(内部的field不含group)
		 * @param key 组合key
		 * @return 是否内部的field
		 */
		public static boolean isInnerField(String key){
			return key.indexOf(SEPERATOR) < 0;
		}
	}
}
