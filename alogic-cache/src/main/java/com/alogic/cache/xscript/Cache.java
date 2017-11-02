package com.alogic.cache.xscript;

import org.apache.commons.lang3.StringUtils;
import com.alogic.cache.context.CacheSource;
import com.alogic.cache.core.CacheStore;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.ServantException;

/**
 * 缓存作用域
 * 
 * @author duanyy
 *
 * @since 1.6.10.5
 * 
 */
public class Cache extends Segment {
	protected String cacheId;
	protected String cacheConn = "cacheConn";
	
	public Cache(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("cache",Cache.class);
		registerModule("cache-expire",CacheClear.class);
		registerModule("cache-load",CacheQuery.class);
	}

	@Override
	public void configure(Properties p){
		cacheId = PropertiesConstants.getString(p,"cacheId",cacheId);
		cacheConn = PropertiesConstants.getString(p,"cacheConn",cacheConn);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		if (StringUtils.isEmpty(cacheId)){
			throw new ServantException("core.cache_not_defined","The relational cache is not defined");
		}
		
		CacheSource cs = CacheSource.get();
		
		CacheStore store = cs.get(cacheId);
		
		if (store == null){
			throw new ServantException("core.cache_not_found","The cache is not found,cacheId=" + cacheId);
		}
		ctx.setObject(cacheConn, store);
		try {
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cacheConn);
		}
	}
}
