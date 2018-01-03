package com.alogic.cache.local;

import java.text.DecimalFormat;
import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.cache.core.AbstractCacheStore;
import com.alogic.cache.core.ExpirePolicy;
import com.alogic.cache.core.MultiFieldObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 基于有限插槽的CacheStore
 * 
 * @author duanyy
 * @since 1.6.3.3
 * @version 1.6.4.9 [20151023 duanyy] <br>
 * - 缓存接口增加set方法 <br>
 * 
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 * 
 * @deprecated
 */
public class SlottedCacheStore extends AbstractCacheStore {
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

	/**
	 * 命中次数
	 */
	protected volatile long hitTimes = 0;
	
	/**
	 * double数值格式化器
	 */
	private static final DecimalFormat DF = new DecimalFormat("#.0000"); 
	
	public long getRequestTimes(){return requestTimes;}	
	
	public long getHitTimes(){return hitTimes;}
	
	public double getHitRate(){return requestTimes <= 0 ? 0 : (double)hitTimes / requestTimes;}
	
	private synchronized void visited(int cnt,boolean hit){
		requestTimes += cnt;
		hitTimes += hit?1:0;
	}	
	
	@Override
	public MultiFieldObject get(String id,boolean cacheAllowed) {
		return load(id,cacheAllowed);
	}

	@Override
	public void expireAll(){
		synchronized (this){
			for (int i = 0 ;i < objectCount ; i ++){
				values[i] = null;
			}
		}
	}
	
	@Override
	public MultiFieldObject expire(String id) {
		//先看看cache中有没有
		int idx = getIndex(id);
		MultiVesionValue mvv = values[idx];
		
		return mvv != null ? mvv.getAndExpire(id, ttl,expirePolicy) : null;
	}	
	
	@Override
	public MultiFieldObject set(String id, MultiFieldObject newValue) {
		//先看看cache中有没有
		int idx = getIndex(id);
	
		MultiVesionValue mvv = values[idx];
		
		MultiFieldObject found = null;
		if (mvv != null){
			found = mvv.get(id, ttl,expirePolicy);
		}		
		
		if (found == null){
			synchronized (this){
				if (mvv == null){
					mvv = new MultiVesionValue(versions);								
					values[idx] = mvv;
				}
				mvv.add(id, found, ttl,expirePolicy);
			}
		}else{
			mvv.replace(id, found, ttl,expirePolicy);
		}
		
		return found;
	}	
	
	@Override
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
					if (found != null){ // NOSONAR
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
	
	@Override
	protected void onConfigure(Element element, Properties p) {
		objectCount = PropertiesConstants.getInt(p, "objectCount", objectCount); // NOSONAR
		if (objectCount <= 0){
			objectCount = 10240;
		}
		
		versions = PropertiesConstants.getInt(p, "versions", versions); // NOSONAR
		if (versions <= 0){
			versions = 3;
		}
		
		values = new MultiVesionValue[objectCount];
	}

	
	@Override
	public void report(Element xml) {
		if (xml != null){
			super.report(xml);
			xml.setAttribute("objectCount",String.valueOf(objectCount));
			xml.setAttribute("versions", String.valueOf(versions));
			xml.setAttribute("requestTimes", String.valueOf(requestTimes));
			xml.setAttribute("hitTimes", String.valueOf(hitTimes));
			xml.setAttribute("hitRate", DF.format(getHitRate()));
		}
	}
	
	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			super.report(json);
			json.put("objectCount", objectCount);
			json.put("versions", versions);
			json.put("requestTimes", requestTimes);
			json.put("hitTimes", hitTimes);
			json.put("hitRate", DF.format(getHitRate()));
		}
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
			
		public MultiFieldObject getAndExpire(String id, int ttl,ExpirePolicy policy) { // NOSONAR
			int verCnt = values.length;			
			for (int i = 0 ;i < verCnt; i ++){
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
			int verCnt = values.length;			
			long now = System.currentTimeMillis();
			for (int i = 0 ;i < verCnt; i ++){
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
		
		public void replace(String id,MultiFieldObject data,int ttl,ExpirePolicy policy){ // NOSONAR
			int verCnt = values.length;			
			long now = System.currentTimeMillis();
			
			for (int i = 0 ;i < verCnt ; i ++){
				if (values[i] != null && values[i].equals(id)){ // NOSONAR
					values[i] = data;
					timestamps[i] = now;
				}
			}			
		}
		
		public void add(String id,MultiFieldObject data,int ttl,ExpirePolicy policy){ // NOSONAR
			int verCnt = values.length;			
			long now = System.currentTimeMillis();
			
			for (int i = 0 ;i < verCnt ; i ++){
				if (values[i] == null){
					values[i] = data;
					timestamps[i] = now;
					return;
				}
			}
			
			//如果位置已满，抛弃第一个值
			if (values[0] != null){
				values[0].expire();
			}
			values[0] = data;
			timestamps[0] = now;
		}
	}	
}
