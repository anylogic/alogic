package com.alogic.xscript;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Properties;

/**
 * Logiclet的上下文
 * 
 * @author duanyy
 * @version 1.6.5.16 [20160713 duanyy] <br>
 * - 不再和Context捆绑 <br>
 * - 
 */
public class LogicletContext extends DefaultProperties {

	protected Map<String,Object> objects = null;

	protected LogicletContext parentContext = null;

	public LogicletContext(Properties p){
		super("default",p);
		if (p instanceof LogicletContext){
			parentContext = (LogicletContext)p;
		}
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
		
		if (found == null && parentContext != null){
			found = parentContext.getObject(id);
		}
		
		return (T)found;
	}
}
