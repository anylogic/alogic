package com.alogic.together.idu;


import java.util.Map;
import com.alogic.cache.core.CacheStore;
import com.alogic.together.AbstractLogiclet;
import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.ServantException;

/**
 * 针对缓存的操作
 * 
 * @author duanyy
 *
 */
public abstract class CacheOperation extends AbstractLogiclet{
	protected String cacheConn = "cacheConn";
	public CacheOperation(String tag, Logiclet p) {
		super(tag, p);
	}
	
	public void configure(Properties p){
		super.configure(p);
		cacheConn = PropertiesConstants.getString(p,"cacheConn", cacheConn);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		CacheStore cache = ctx.getObject(cacheConn);
		if (cache == null){
			throw new ServantException("core.no_cache","It must be in a cache context,check your together script.");
		}
		onExecute(cache,root,current,ctx,watcher);
	}

	protected abstract void onExecute(CacheStore cache, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher);
}