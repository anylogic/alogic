package com.alogic.together;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.anysoft.util.DefaultProperties;
import com.logicbus.backend.Context;

/**
 * Logiclet的上下文
 * 
 * @author duanyy
 *
 */
public class LogicletContext extends DefaultProperties {

	protected Map<String,Object> objects = null;

	protected LogicletContext parent = null;

	protected Context context = null;
	
	public LogicletContext(LogicletContext p,Context ctx){
		super("default",p==null?ctx:p);
		parent = p;
		context = ctx;
	}
	
	public void setObject(String id,Object object){
		if (objects == null){
			objects = new ConcurrentHashMap<String,Object>();
		}
		
		objects.put(id, object);
	}
	
	public void removeObject(String id){
		if (objects != null){
			objects.remove(id);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getObject(String id){
		Object found = null;
		
		if (objects != null){
			found = objects.get(id);
		}
		
		if (found == null && parent != null){
			found = parent.getObject(id);
		}
		
		return (T)found;
	}
	
	public Context getContext(){
		return context;
	}
}
