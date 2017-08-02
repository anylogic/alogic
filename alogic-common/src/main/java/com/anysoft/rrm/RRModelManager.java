package com.anysoft.rrm;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.anysoft.util.Properties;

/**
 * 管理器
 * @author duanyy
 * @version 1.6.4.14 [20151126 duanyy] <br>
 * - 增加list方法.<br>
 * 
 * @version 1.9.6.7 [20170802 duanyy] <br>
 * - 修正多实例下的并发问题 <br>
 * 
 */
public class RRModelManager {
	
	private Map<String,RRModel<? extends RRData>> rrms = new ConcurrentHashMap<String,RRModel<? extends RRData>>();
	
	public RRModel<? extends RRData> getModel(String id){
		return (RRModel<? extends RRData>) rrms.get(id);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <data extends RRData> RRModel<data> addModel(String id,Class<data> clazz,Properties p){
		RRModel<data> found = (RRModel<data>) rrms.get(id);
		if (found == null){
			found = new RRModel<data>(id);		
			found.configure(p);
			rrms.put(id, found);
		}
		return found;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <data extends RRData> RRModel<data> addModel(String id,data instance,Properties p){
		RRModel<data> found = (RRModel<data>) rrms.get(id);
		if (found == null){
			found = new RRModel<data>(id);
			
			found.configure(p);
			found.update(System.currentTimeMillis(), instance);
			
			rrms.put(id, found);
		}
		return found;
	}	
	
	public <data extends RRData> RRModel<data> addModel(String id,RRModel<data> newModel){
		rrms.put(id, newModel);
		return newModel;
	}	
	
	public void remove(String id){
		rrms.remove(id);
	}
		
	public void clear(){
		rrms.clear();
	}
	
	public Collection<RRModel<? extends RRData>> list(){
		return rrms.values();
	}

	protected static RRModelManager instance = new RRModelManager();
	
	public static synchronized RRModelManager get(){
		return instance;
	}
}
