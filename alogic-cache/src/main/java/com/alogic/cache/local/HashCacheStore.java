package com.alogic.cache.local;

import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.cache.core.AbstractCacheStore;
import com.alogic.cache.core.MultiFieldObject;
import com.anysoft.util.Properties;

/**
 * 基于Hashtable的CacheStore
 * 
 * @author duanyy
 * 
 * @since 1.6.3.3
 * @version 1.6.4.9 [20151023 duanyy] <br>
 * - 缓存接口增加set方法 <br>
 * 
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 */
public class HashCacheStore extends AbstractCacheStore{
	
	/**
	 * double数值格式化器
	 */
	private static DecimalFormat df = new DecimalFormat("#.0000"); 
	
	protected Hashtable<String,MultiFieldObject> values = new Hashtable<String,MultiFieldObject>(); // NOSONAR
	
	/**
	 * 请求次数
	 */
	protected volatile long requestTimes = 0;
	
	/**
	 * 命中次数
	 */
	protected volatile long hitTimes = 0;
	
	public long getRequestTimes(){return requestTimes;}	
	
	public long getHitTimes(){return hitTimes;}
	
	public double getHitRate(){return requestTimes <= 0 ? 0 : (double)hitTimes / requestTimes;}
	
	private synchronized void visited(int cnt,boolean hit){
		requestTimes += cnt;
		hitTimes += hit?1:0;
	}
	
	@Override
	public MultiFieldObject get(String id, boolean cacheAllowed) {
		return load(id,cacheAllowed);
	}

	@Override
	public MultiFieldObject expire(String id) {
		return values.remove(id);
	}

	@Override
	public void expireAll() {
		values.clear();
	}

	@Override
	public MultiFieldObject set(String id, MultiFieldObject newValue) {
		return values.put(id, newValue);
	}
	
	@Override
	public MultiFieldObject load(String id, boolean cacheAllowed) {
		if (!cacheAllowed){
			return provider.load(id,cacheAllowed);
		}
		
		boolean hit = false;
		
		try {
			//先看看cache中有没有
			MultiFieldObject found = values.get(id);
			if (found == null){
				if (provider != null){
					found = provider.load(id, true);
					if (found != null){ // NOSONAR
						synchronized (this){
							values.put(id, found);
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

	@Override
	protected void onConfigure(Element e, Properties p) {
		// nothing to do
	}

	@Override
	public void report(Element xml) {
		if (xml != null){
			super.report(xml);
			xml.setAttribute("objectCount",String.valueOf(values.size()));
			xml.setAttribute("requestTimes", String.valueOf(requestTimes));
			xml.setAttribute("hitTimes", String.valueOf(hitTimes));
			xml.setAttribute("hitRate", df.format(getHitRate()));
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			super.report(json);
			json.put("objectCount", values.size());
			json.put("requestTimes", requestTimes);
			json.put("hitTimes", hitTimes);
			json.put("hitRate", df.format(getHitRate()));
		}
	}

}
