package com.alogic.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.load.HashObject;
import com.alogic.load.SetObject;
import com.anysoft.util.JsonSerializer;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Pair;
import com.anysoft.util.StringMatcher;
import com.anysoft.util.XmlTools;


/**
 * 缓存对象
 * @author yyduan
 * @since 1.6.11.6
 * 
 * @version 1.6.11.8 [20180109] duanyy <br>
 * - 优化缓存相关的xscript插件 <br>
 */
public interface CacheObject extends HashObject,SetObject,JsonSerializer{
	/**
	 * 缺省的信息组
	 */
	public static final String DEFAULT_GROUP = "$default";
	
	/**
	 * 复制到另一个对象
	 * @param another 另一个对象
	 */
	public void copyTo(CacheObject another);
	
	/**
	 * 是否有效对象
	 * @return 是否有效对象
	 */
	public boolean isValid();
	
	/**
	 * 虚基类
	 * @author yyduan
	 *
	 */
	public abstract static class Abstract implements CacheObject{

		/**
		 * 创建的时间戳
		 */
		protected long timestamp = System.currentTimeMillis();

		/**
		 * id
		 */
		protected String id;
		
		public Abstract(final String id){
			this.id = id;
		}
		
		public Abstract(){
		}
		
		@Override
		public long getTimestamp() {
			return timestamp;
		}

		@Override
		public boolean isExpired() {
			return false;
		}

		@Override
		public void expire() {
		}

		@Override
		public String getId() {
			return id;
		}
		
		@Override
		public boolean isValid() {
			return StringUtils.isNotEmpty(id);
		}

		/**
		 * 获取Hash信息组
		 * 
		 * @param group 信息组id
		 * @param create 是否创建，当信息组不存在的时候，如果create为true，则创建一个
		 * @return Hash信息组实例
		 */
		protected abstract Map<String,String> getMapObject(String group,boolean create);
		
		/**
		 * 获取Set信息组
		 * @param group 信息组id
		 * @param create 是否创建，当信息组不存在的时候，如果create为true，则创建一个
		 * @return Set信息组实例
		 */
		protected abstract Set<String> getSetObject(String group,boolean create);
		
		protected boolean isConditionValid(String condition){
			return StringUtils.isNotEmpty(condition) && !condition.equals("*");
		}
		
		@Override
		public void hSet(String group, String key, String value,
				boolean overwrite) {
			Map<String,String> map = getMapObject(group,true);
			if (map != null){
				boolean exist = map.containsKey(key);
				if (!exist || overwrite){
					map.put(key,value);
				}
			}
		}

		@Override
		public String hGet(String group, String key, String dftValue) {
			Map<String,String> map = getMapObject(group,false);
			return (map != null && map.containsKey(key))?map.get(key):dftValue;
		}

		@Override
		public boolean hExist(String group, String key) {
			Map<String,String> map = getMapObject(group,false);
			return (map != null && map.containsKey(key));
		}

		@Override
		public List<Pair<String, String>> hGetAll(String group, String condition) {
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
		public List<String> hKeys(String group, String condition) {
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
		public void hDel(String group, String key) {
			Map<String,String> mapObject = getMapObject(group,false);
			if (mapObject != null){
				mapObject.remove(key);
			}
		}

		@Override
		public void hDel(String group) {
			Map<String,String> mapObject = getMapObject(group,false);
			if (mapObject != null){
				mapObject.clear();
			}
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
		public void sAdd(String group, String... members) {
			Set<String> obj = getSetObject(group,true);
			if (obj != null){
				for (String m:members){
					obj.add(m);
				}
			}
		}

		@Override
		public void sDel(String group, String... members) {
			Set<String> obj = getSetObject(group,false);
			if (obj != null){
				for (String m:members){
					obj.remove(m);
				}
			}
		}

		@Override
		public void sDel(String group) {
			Set<String> obj = getSetObject(group,false);
			if (obj != null){
				obj.clear();
			}
		}

		@Override
		public int sSize(String group) {
			Set<String> obj = getSetObject(group,false);
			return obj == null ? 0 : obj.size();
		}

		@Override
		public List<String> sMembers(String group, String condition) {
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
		public boolean sExist(String group, String member) {
			Set<String> obj = getSetObject(group,false);
			return obj != null ? obj.contains(member) : false;
		}

		@Override
		public String getValue(String varName, Object context,
				String defaultValue) {
			return getRawValue(varName,context,defaultValue);
		}

		@Override
		public String getRawValue(String varName, Object context,
				String dftValue) {
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
	}
	
	/**
	 * 简单实现，仅支持缺省Hash信息组
	 * @author yyduan
	 *
	 */
	public static class Simple extends Abstract{
		protected Map<String,String> keyvalues = null;
		protected Set<String> sets = null;
		
		public Simple() {
		}
		
		public Simple(String id) {
			super(id);
		}

		@Override
		public void copyTo(CacheObject another) {
			if (another != null){
				Map<String, String> map = getMapObject(DEFAULT_GROUP,false);
				if (map != null){
					Iterator<Entry<String,String>> iter = map.entrySet().iterator();
					
					while (iter.hasNext()){
						Entry<String,String> e = iter.next();
						another.hSet(DEFAULT_GROUP, e.getKey(), e.getValue(), true);
					}
				}
				Set<String> set = getSetObject(DEFAULT_GROUP,false);
				if (set != null){
					Iterator<String> iter = set.iterator();
					
					while(iter.hasNext()){
						another.sAdd(DEFAULT_GROUP, iter.next());
					}
				}
			}
		}

		@Override
		protected Map<String, String> getMapObject(String group, boolean create) {
			if (keyvalues == null && create){
				synchronized(this){
					if (keyvalues == null){
						keyvalues = new HashMap<String,String>();
					}
				}
			}
			
			return keyvalues;
		}

		@Override
		protected Set<String> getSetObject(String group, boolean create) {
			if (sets == null && create){
				synchronized(this){
					if (sets == null){
						sets = Collections.newSetFromMap(new ConcurrentHashMap<String,Boolean>());
					}
				}
			}
			
			return sets;
		}

		@Override
		public void toJson(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"id",getId());
				
				Map<String, String> map = getMapObject(DEFAULT_GROUP,false);
				if (map != null){
					Iterator<Entry<String,String>> iter = map.entrySet().iterator();
					while (iter.hasNext()){
						Entry<String,String> keyvalue = iter.next();
						JsonTools.setString(json,keyvalue.getKey(),keyvalue.getValue());
					}
				}
			}
		}

		@Override
		public void fromJson(Map<String, Object> json) {
			if (json != null){
				id = JsonTools.getString(json, "id", "");
				
				Iterator<Entry<String,Object>> iter = json.entrySet().iterator();
				
				Map<String, String> map = getMapObject(DEFAULT_GROUP,true);
				while (iter.hasNext()){
					Entry<String,Object> keyvalue = iter.next();
					map.put(keyvalue.getKey(), keyvalue.getValue().toString());
				}
			}
		}

	}
}
