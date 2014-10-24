package com.anysoft.cache;


import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Watcher;


/**
 * 基于Hash的缓存队列
 * 
 * @author duanyy
 *
 * @param <data>
 * 
 * @since 1.3.0
 * 
 * @version 1.5.2 [20141017 duanyy]
 * - 淘汰ChangeAware机制，采用更为通用的Watcher
 * 
 */
public class HashCachedDataStore<data extends Cacheable> implements DataStore<data> {
	
	/**
	 * 后端的DataStore
	 */
	protected DataStore<data> dataStore = null;
	
	public void setDataStore(DataStore<data> _dataStore){
		dataStore = _dataStore;
	}
	
	@SuppressWarnings("unchecked")
	
	public void save(String id, data _data) throws BaseException {
		if (dataStore != null){
			dataStore.save(id, _data);
		}
		
		int idx = getIndex(id);		
		MultiVesionValue<data> mvv = (MultiVesionValue<data>)values[idx];
		if (mvv != null){
			mvv.add(id, _data, ttl);
		}	
	}

	
	public data load(String id) {
		return load(id,true);
	}

	@SuppressWarnings("unchecked")
	
	public data load(String id, boolean cacheAllowed) {
		if (!cacheAllowed){
			return dataStore.load(id,cacheAllowed);
		}
		
		boolean hit = false;
		
		try {
			//先看看cache中有没有
			int idx = getIndex(id);
		
			MultiVesionValue<data> mvv = (MultiVesionValue<data>)values[idx];
			data found = null;
			if (mvv != null){
				found = mvv.get(id, ttl);
			}
	
			if (found == null){
				if (dataStore != null){
					found = dataStore.load(id, true);
					if (found != null){
						synchronized (this){
							if (mvv == null){
								mvv = new MultiVesionValue<data>(versions);								
								values[idx] = mvv;
							}
							mvv.add(id, found, ttl);
						}					
					}
				}
			}else{
				hit = true;
			}
			return found;
		}finally{
			visited(1,hit);
		}
	}
	
	private int getIndex(String id){
		int hash = id.hashCode();
		
		hash = hash <= 0 ? -hash : hash;
		
		return hash % objectCount;
	}

	
	public void addWatcher(Watcher<data> listener) {
		if (dataStore != null){
			dataStore.addWatcher(listener);
		}
	}

	
	public void removeWatcher(Watcher<data> listener) {
		if (dataStore != null){
			dataStore.removeWatcher(listener);
		}
	}

	
	public void close() throws Exception {
		if (dataStore != null){
			dataStore.close();
		}
	}

	
	public void create(Properties props) throws BaseException {
		objectCount = PropertiesConstants.getInt(props, "objectCount", objectCount);
		if (objectCount <= 0){
			objectCount = 10240;
		}
		
		versions = PropertiesConstants.getInt(props, "versions", versions);
		if (versions <= 0){
			versions = 3;
		}
		
		values = new MultiVesionValue<?>[objectCount];
	}

	
	public void refresh() throws BaseException {
		//清除当前缓存
		synchronized (this){
			for (int i = 0 ;i < values.length ; i ++){
				values[i] = null;
			}
		}
		hitTimes = 0;
		requestTimes = 0;
	}
	
	/**
	 * 对象个数
	 */
	protected int objectCount = 10240;
	
	/**
	 * 版本数
	 */
	protected int versions = 3;
	
	/**
	 * Time To Live
	 */
	protected int ttl = 30 * 60 * 1000;
	
	protected MultiVesionValue<?>[] values = null;

	/**
	 * 请求次数
	 */
	protected volatile long requestTimes = 0;
	
	public long getRequestTimes(){return requestTimes;}
	
	/**
	 * 命中次数
	 */
	protected volatile long hitTimes = 0;
	
	public long getHitTimes(){return hitTimes;}
	
	public double getHitRate(){return requestTimes <= 0 ? 0 : (double)hitTimes / requestTimes;}
	
	private synchronized void visited(int cnt,boolean hit){
		requestTimes += cnt;
		hitTimes += (hit)?1:0;
	}
	
	/**
	 * 缓存节点对象
	 * @author duanyy
	 *
	 * @param <data>
	 */
	public static class MultiVesionValue<data extends Cacheable> {
		protected Object[] values = null;
		protected long [] timestamps = null;
		
		protected MultiVesionValue(int versions){
			values = new Object[versions];
			timestamps = new long[versions];
		}
		
		@SuppressWarnings("unchecked")
		public data get(String id,int ttl){
			int versions = values.length;			
			long now = System.currentTimeMillis() - ttl;
			for (int i = 0 ;i < versions; i ++){
				data found = (data)values[i];				
				if (found != null && id.equals(found.getId()) && timestamps[i] > now){
					//got
					return found;
				}
			}
			return null;
		}
		
		public void add(String id,data _data,int ttl){
			int versions = values.length;			
			long now = System.currentTimeMillis();
			
			for (int i = 0 ;i < versions ; i ++){
				if (values[i] == null){
					values[i] = _data;
					timestamps[i] = now;
					return;
				}
			}
			
			values[0] = _data;
			timestamps[0] = now;
		}
	}
	

	
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("objectCount",String.valueOf(objectCount));
			xml.setAttribute("versions", String.valueOf(versions));
			xml.setAttribute("requestTimes", String.valueOf(requestTimes));
			xml.setAttribute("hitTimes", String.valueOf(hitTimes));
		}
	}

	
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("objectCount", objectCount);
			json.put("versions", versions);
			json.put("requestTimes", requestTimes);
			json.put("hitTimes", hitTimes);
		}
	}
}
