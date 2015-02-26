package com.alogic.cache.local;

import java.text.DecimalFormat;
import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.cache.core.AbstractCacheStore;
import com.alogic.cache.core.ExpirePolicy;
import com.alogic.cache.core.MultiFieldObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Watcher;

/**
 * 本地缓存实现
 * 
 * @author duanyy
 * @since 1.6.3.3
 * 
 */
public class LocalCacheStore extends AbstractCacheStore {
	public MultiFieldObject get(String id,boolean cacheAllowed) {
		return load(id,cacheAllowed);
	}

	public void expireAll(){
		synchronized (this){
			for (int i = 0 ;i < objectCount ; i ++){
				values[i] = null;
			}
		}
	}
	
	public MultiFieldObject expire(String id) {
		//先看看cache中有没有
		int idx = getIndex(id);
		MultiVesionValue mvv = values[idx];
		
		return mvv != null ? mvv.getAndExpire(id, ttl,expirePolicy) : null;
	}	
	
	public MultiFieldObject load(String id, boolean cacheAllowed) {
		if (!cacheAllowed){
			return provider.load(id,cacheAllowed);
		}
		
		boolean hit = false;
		
		try {
			//先看看cache中有没有
			int idx = getIndex(id);
		
			MultiVesionValue mvv = values[idx];
			MultiFieldObject found = null;
			if (mvv != null){
				found = mvv.get(id, ttl,expirePolicy);
			}
	
			if (found == null){
				if (provider != null){
					found = provider.load(id, true);
					if (found != null){
						synchronized (this){
							if (mvv == null){
								mvv = new MultiVesionValue(versions);								
								values[idx] = mvv;
							}
							mvv.add(id, found, ttl,expirePolicy);
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
		return (id.hashCode() & Integer.MAX_VALUE) % objectCount;
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
	
	protected MultiVesionValue[] values = null;

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
	
	public void addWatcher(Watcher<MultiFieldObject> watcher) {
		if (provider != null){
			provider.addWatcher(watcher);
		}
	}

	public void removeWatcher(Watcher<MultiFieldObject> watcher) {
		if (provider != null){
			provider.addWatcher(watcher);
		}
	}

	@Override
	protected void onConfigure(Element _e, Properties p) {
		objectCount = PropertiesConstants.getInt(p, "objectCount", objectCount);
		if (objectCount <= 0){
			objectCount = 10240;
		}
		
		versions = PropertiesConstants.getInt(p, "versions", versions);
		if (versions <= 0){
			versions = 3;
		}
		
		values = new MultiVesionValue[objectCount];
	}

	/**
	 * 多版本缓存对象
	 * 
	 * @author duanyy
	 *
	 */
	public static class MultiVesionValue {
		protected MultiFieldObject[] values = null;
		protected long [] timestamps = null;
		
		protected MultiVesionValue(int versions){
			values = new MultiFieldObject[versions];
			timestamps = new long[versions];
		}
		
		public MultiFieldObject getAndExpire(String id, int ttl,ExpirePolicy policy) {
			int versions = values.length;			
			for (int i = 0 ;i < versions; i ++){
				MultiFieldObject found = values[i];				
				if (found != null && id.equals(found.getId())){
					found.expire();
					values[i] = null;
					return found;
				}
			}
			return null;
		}

		public MultiFieldObject get(String id,int ttl,ExpirePolicy policy){
			int versions = values.length;			
			long now = System.currentTimeMillis();
			for (int i = 0 ;i < versions; i ++){
				MultiFieldObject found = values[i];				
				if (found != null && id.equals(found.getId())){
					if (policy.isExpired(found, timestamps[i], now, ttl)){
						//该值已经过期，抛弃
						found.expire();
						values[i] = null;
					}else{
						return found;
					}
				}
			}
			return null;
		}
		
		public void add(String id,MultiFieldObject _data,int ttl,ExpirePolicy policy){
			int versions = values.length;			
			long now = System.currentTimeMillis();
			
			for (int i = 0 ;i < versions ; i ++){
				if (values[i] == null){
					values[i] = _data;
					timestamps[i] = now;
					return;
				}
			}
			
			//如果位置已满，抛弃第一个值
			if (values[0] != null){
				values[0].expire();
			}
			values[0] = _data;
			timestamps[0] = now;
		}
	}
	/**
	 * double数值格式化器
	 */
	private static DecimalFormat df = new DecimalFormat("#.0000"); 
	
	public void report(Element xml) {
		if (xml != null){
			super.report(xml);
			xml.setAttribute("objectCount",String.valueOf(objectCount));
			xml.setAttribute("versions", String.valueOf(versions));
			xml.setAttribute("requestTimes", String.valueOf(requestTimes));
			xml.setAttribute("hitTimes", String.valueOf(hitTimes));
			xml.setAttribute("hitRate", df.format(getHitRate()));
		}
	}

	public void report(Map<String, Object> json) {
		if (json != null){
			super.report(json);
			json.put("objectCount", objectCount);
			json.put("versions", versions);
			json.put("requestTimes", requestTimes);
			json.put("hitTimes", hitTimes);
			json.put("hitRate", df.format(getHitRate()));
		}
	}

}
