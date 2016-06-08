package com.alogic.together.idu;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.cache.context.CacheSource;
import com.alogic.cache.core.CacheStore;
import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
import com.alogic.together.plugins.Segment;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.ServantException;

/**
 * 缓存作用域
 * 
 * @author duanyy
 *
 */
public class Cache extends Segment {
	protected String cacheId;
	protected String cacheConn = "cacheConn";
	
	public Cache(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		cacheId = PropertiesConstants.getString(p,"cacheId",cacheId);
		cacheConn = PropertiesConstants.getString(p,"cacheConn",cacheConn);
	}
	
	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
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
