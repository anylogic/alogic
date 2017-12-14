package com.alogic.cache.session;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Element;

import com.alogic.cache.core.AbstractCacheStore;
import com.alogic.cache.core.MultiFieldObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.context.KValueSource;
import com.logicbus.kvalue.core.HashRow;
import com.logicbus.kvalue.core.Schema;
import com.logicbus.kvalue.core.SetRow;
import com.logicbus.kvalue.core.Table;

/**
 * 基于KValue的缓存实现
 * 
 * @author duanyy
 *
 */
public class KValueCacheStore extends AbstractCacheStore {
	/**
	 * double数值格式化器
	 */
	private static DecimalFormat df = new DecimalFormat("#.0000"); 	
	/**
	 * 用于保存Hash类数据的表
	 */
	protected Table hashTable;
	/**
	 * 用于保存Set类数据的表
	 */
	protected Table setTable;
	
	/**
	 * 请求次数
	 */
	protected volatile long requestTimes = 0;
	
	/**
	 * 命中次数
	 */
	protected volatile long hitTimes = 0;
	
	/**
	 * 缓存生存时间，分钟为单位
	 */
	protected int ttl = 24 * 60;
	
	public long getRequestTimes(){return requestTimes;}	
	
	public long getHitTimes(){return hitTimes;}
	
	public double getHitRate(){return requestTimes <= 0 ? 0 : (double)hitTimes / requestTimes;}
	
	private synchronized void visited(int cnt,boolean hit){
		requestTimes += cnt;
		hitTimes += hit?1:0;
	}	
	
	@Override
	public void report(Element xml) {
		if (xml != null){
			super.report(xml);
			xml.setAttribute("requestTimes", String.valueOf(requestTimes));
			xml.setAttribute("hitTimes", String.valueOf(hitTimes));
			xml.setAttribute("hitRate", df.format(getHitRate()));
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			super.report(json);
			json.put("requestTimes", requestTimes);
			json.put("hitTimes", hitTimes);
			json.put("hitRate", df.format(getHitRate()));
		}
	}	
	
	@Override
	public MultiFieldObject get(String id, boolean cacheAllowed) {
		return load(id,cacheAllowed);
	}

	protected String getRowId(String id){
		return id() + '$' + id;
	}
	
	@Override
	public MultiFieldObject expire(String id) {
		KValueMultiFieldObject object = getCachedObject(id);
		object.expire();
		return null;
	}

	@Override
	public MultiFieldObject set(String id, MultiFieldObject newValue) {
		KValueMultiFieldObject object = getCachedObject(id);
		object.visited();
		if (newValue != null){
			newValue.copyTo(object);
		}
		
		return object;
	}
	
	protected KValueMultiFieldObject getCachedObject(String id){
		String rowId = getRowId(id);
		HashRow hash = (HashRow) hashTable.select(rowId, true);
		SetRow set = (SetRow) setTable.select(rowId, true);
		return new KValueMultiFieldObject(id,hash,set,ttl);		
	}

	@Override
	public void expireAll() {
		// can not do this
	}

	@Override
	public MultiFieldObject load(String id, boolean cacheAllowed) {
		if (!cacheAllowed){
			return provider.load(id,cacheAllowed);
		}
		
		boolean hit = false;
		
		try {
			//先看看cache中有没有
			KValueMultiFieldObject found = getCachedObject(id);

			if (!found.exists()){
				if (provider != null){
					MultiFieldObject newObject = provider.load(id, true);
					if (newObject != null){ // NOSONAR
						newObject.copyTo(found);
					}
				}
			}else{
				hit = true;
			}
			return found.exists() ? found : null;
		}finally{
			visited(1,hit);
		}
	}

	@Override
	protected void onConfigure(Element e, Properties p) {
		String schema = PropertiesConstants.getString(p,"schema","redis");
		String hashTableName = PropertiesConstants.getString(p,"table.hash","m");
		String setTableName = PropertiesConstants.getString(p,"table.set","s");
		
		Schema instance = KValueSource.getSchema(schema);
		if (instance == null){
			throw new BaseException("core.e1003","Can not find a kvalue schema named " + schema);
		}
		
		ttl = PropertiesConstants.getInt(p, "ttl", ttl);
		
		hashTable = instance.getTable(hashTableName);
		if (hashTable == null){
			throw new BaseException("core.e1003","Can not find a kvalue table named " + hashTable);
		}
		
		setTable = instance.getTable(setTableName);
		if (setTable == null){
			throw new BaseException("core.e1003","Can not find a kvalue table named " + setTableName);
		}		
	}

	/**
	 * 基于KValue的缓存对象
	 * 
	 * @author duanyy
	 * @version 1.6.4.43 [20160411 duanyy] <br>
	 * - DataProvider增加获取原始值接口 <br>
	 */
	public static class KValueMultiFieldObject implements MultiFieldObject {
		protected String id;
		protected HashRow hash = null;
		protected SetRow set = null;
		protected static final char SEPERATOR = '$';
		protected long lastVisitedTime = 0;
		protected int ttl = 24 * 60;
		
		public KValueMultiFieldObject(String objectId,HashRow hashRow,SetRow setRow,int timeToLive){
			id = objectId;
			hash = hashRow;
			set = setRow;
			ttl = timeToLive;
		}
		
		protected String extractId(String key){
			int idx = key.indexOf(SEPERATOR);
			if (idx < 0){
				return "";
			}
			return key.substring(0,idx);			
		}
		
		protected String extractField(String key){
			int idx = key.indexOf(SEPERATOR);
			if (idx < 0){
				return key;
			}
			return key.substring(idx + 1);
		}	
		
		protected boolean isField(String key,String id){
			return key.startsWith(id + SEPERATOR);
		}
		
		protected boolean isInnerField(String key){
			return key.indexOf(SEPERATOR) < 0;
		}
		
		protected void visited(){
			long now = System.currentTimeMillis();
			if (now - lastVisitedTime > 60 * 1000){
				//上次写入大于一分钟，才写缓存，避免多次调用
				lastVisitedTime = now;
				hash.set("t", lastVisitedTime);
				hash.ttl(ttl,TimeUnit.MINUTES);
				set.ttl(ttl, TimeUnit.MINUTES);
			}
		}		
		
		public boolean exists(){
			if (lastVisitedTime > 0){
				return true;
			}
			lastVisitedTime = hash.get("t", 0);
			return lastVisitedTime > 0;
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
			hash.delete();
			set.delete();
		}

		@Override
		public void toXML(Element e) {
			// nothing to do
		}

		@Override
		public void fromXML(Element e) {
			// nothing to do
		}

		@Override
		public void toJson(Map<String, Object> json) {
			if (json == null){
				return ;
			}
			if (hash != null){
				Map<String,String> all = hash.getAll();
				
				Iterator<Entry<String,String>> iterator = all.entrySet().iterator();
				
				Map<String,Map<String,Object>> hashes = new HashMap<String,Map<String,Object>>();
				while (iterator.hasNext()){
					Entry<String,String> entry = iterator.next();
					String key = entry.getKey();
					
					if (isInnerField(key)){
						continue;
					}
					
					String id = extractId(key);
					String field = extractField(key);
					String value = entry.getValue();
					if (id == null || id.length() <= 0){
						json.put(field, value);
						continue;
					}
					
					Map<String,Object> currentHash = hashes.get(id);
					if (currentHash == null){
						currentHash = new HashMap<String,Object>();
						hashes.put(id,currentHash);
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
					String id = extractId(key);
					String value = extractField(key);
					
					List<String> currentSet = sets.get(id);
					if (currentSet == null){
						currentSet = new ArrayList<String>();
						sets.put(id, currentSet);
					}
					currentSet.add(value);		
				}
				
				if (!sets.isEmpty()){
					json.put("set", sets);
				}
			}
		}

		@Override
		public void fromJson(Map<String, Object> json) {
			// nothing to do
		}

		@Override
		public String getValue(String varName, Object context,
				String defaultValue) {
			visited();
			String value = hash.get(SEPERATOR + varName, defaultValue);
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
		public void setField(String key, String value) {
			visited();
			hash.set(SEPERATOR + key,value);
		}

		@Override
		public String getField(String key, String dftValue) {
			visited();
			String value = hash.get(SEPERATOR + key, dftValue);
			return value == null ? dftValue : value;
		}

		@Override
		public String hGet(String id, String field, String dftValue) {
			visited();
			String value = hash.get(id + SEPERATOR + field, dftValue);
			return value == null ? dftValue : value;
		}

		@Override
		public void hSet(String id, String field, String value) {
			visited();
			hash.set(id + SEPERATOR + field, value);
		}

		@Override
		public boolean hExist(String id, String field) {
			visited();
			return hash.exists(id + SEPERATOR + field);
		}

		@Override
		public Map<String, String> hGetAll(String id) {
			visited();
			Map<String,String> all = hash.getAll();
			Map<String,String> filtered = new HashMap<String,String>();
			
			Iterator<Entry<String,String>> iterator = all.entrySet().iterator();
			
			while (iterator.hasNext()){
				Entry<String,String> entry = iterator.next();
				
				String key = entry.getKey();
				if (isField(key,id)){
					filtered.put(extractField(key), entry.getValue());
				}
			}
			
			return filtered;
		}

		@Override
		public int hLen(String id) {
			visited();
			Map<String,String> all = hash.getAll();
		
			Iterator<Entry<String,String>> iterator = all.entrySet().iterator();

			int count = 0;
			while (iterator.hasNext()){
				Entry<String,String> entry = iterator.next();
				
				String key = entry.getKey();
				if (isField(key,id)){
					count ++;
				}
			}
			
			return count;
		}

		@Override
		public String[] hKeys(String id) {
			visited();
			List<String> all = hash.keys();
			List<String> keys = new ArrayList<String>();
			
			for (String key:all){
				if (isField(key,id)){
					keys.add(extractField(key));
				}				
			}
			
			return keys.toArray(new String[keys.size()]);
		}
		
		@Override
		public String[] hValues(String id) {
			visited();
			Map<String,String> all = hash.getAll();
			List<String> values = new ArrayList<String>();
			
			Iterator<Entry<String,String>> iterator = all.entrySet().iterator();
			
			while (iterator.hasNext()){
				Entry<String,String> entry = iterator.next();
				
				String key = entry.getKey();
				if (isField(key,id)){
					values.add(entry.getValue());
				}
			}
			
			return values.toArray(new String[values.size()]);
		}

		@Override
		public void sAdd(String id, String... member) {
			visited();
			for (String m:member){
				set.add(id + SEPERATOR + m);
			}
		}

		@Override
		public void sDel(String id, String... member) {
			visited();
			for (String m:member){
				set.remove(id + SEPERATOR + m);
			}
		}

		@Override
		public int sSize(String id) {
			visited();
			List<String> all = set.getAll();
			
			int count = 0;
			
			for (String m:all){
				if (isField(m,id)){
					count ++;
				}
			}
			
			return count;
		}

		@Override
		public String[] sMembers(String id) {
			visited();
			List<String> all = set.getAll();
			List<String> members = new ArrayList<String>();

			for (String m:all){
				if (isField(m,id)){
					members.add(extractField(m));
				}
			}
			
			return members.toArray(new String[members.size()]);
		}
		
		@Override
		public boolean sExist(String id,String member){
			visited();
			return set.contain(id + SEPERATOR + member);
		}

		@Override
		public void del(String id) {
			visited();
			hash.del(SEPERATOR + id);
		}

		@Override
		public void copyTo(MultiFieldObject another) {
			if (another != null){
				if (hash != null){
					Map<String,String> all = hash.getAll();
					
					Iterator<Entry<String,String>> iter = all.entrySet().iterator();
					
					while (iter.hasNext()){
						Entry<String,String> entry = iter.next();
						
						String id = extractId(entry.getKey());
						String field = extractField(entry.getKey());
						
						if (id == null || id.length() <= 0){
							another.setField(field,entry.getValue());
						}else{
							another.hSet(id, field, entry.getValue());
						}
					}
				}
				if (set != null){
					List<String> all = set.getAll();
					for (String member:all){
						String id = extractId(member);
						String value = extractField(member);
						if (id != null && id.length() > 0){
							another.sAdd(id, value);
						}						
					}
				}
			}
		}

		@Override
		public long getLastVisitedTime() {
			return hash.get("t", System.currentTimeMillis());
		}

		@Override
		public String[] keys() {
			return hKeys("");
		}

		@Override
		public int count() {
			return hLen("");
		}


	}
}
