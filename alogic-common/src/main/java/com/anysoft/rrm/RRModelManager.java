package com.anysoft.rrm;

import java.util.Hashtable;

import com.anysoft.util.Properties;

/**
 * 管理器
 * @author duanyy
 *
 */
public class RRModelManager {
	
	private Hashtable<String,Object> rrms = new Hashtable<String,Object>();
	
	@SuppressWarnings("unchecked")
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

	protected static RRModelManager instance = new RRModelManager();
	
	public static synchronized RRModelManager get(){
		return instance;
	}
}
