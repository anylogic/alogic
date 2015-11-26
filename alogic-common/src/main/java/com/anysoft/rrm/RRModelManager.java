package com.anysoft.rrm;

import java.util.Collection;
import java.util.Hashtable;

import com.anysoft.util.Properties;

/**
 * 管理器
 * @author duanyy
 * @version 1.6.4.14 [20151126 duanyy] <br>
 * - 增加list方法.<br>
 */
public class RRModelManager {
	
	private Hashtable<String,RRModel<? extends RRData>> rrms = new Hashtable<String,RRModel<? extends RRData>>();
	
	public RRModel<? extends RRData> getModel(String id){
		return (RRModel<? extends RRData>) rrms.get(id);
	}
	
	public <data extends RRData> RRModel<data> addModel(String id,Class<data> clazz,Properties p){
		RRModel<data> newModel = new RRModel<data>(id);		
		newModel.configure(p);
		rrms.put(id, newModel);
		
		return newModel;
	}
	
	public <data extends RRData> RRModel<data> addModel(String id,data instance,Properties p){
		RRModel<data> newModel = new RRModel<data>(id);
		
		newModel.configure(p);
		newModel.update(System.currentTimeMillis(), instance);
		
		rrms.put(id, newModel);
		return newModel;
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
