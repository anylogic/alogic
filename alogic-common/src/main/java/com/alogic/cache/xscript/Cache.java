package com.alogic.cache.xscript;

import org.apache.commons.lang3.StringUtils;

import com.alogic.cache.CacheObject;
import com.alogic.cache.naming.CacheStoreFactory;
import com.alogic.load.Store;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 缓存作用域
 * 
 * @author duanyy
 *
 * @since 1.6.10.5
 * @version 1.6.11.6 [20180103 duanyy] <br>
 * - 从alogic-cache中迁移过来
 * 
 * @version 1.6.11.8 [20180109] duanyy <br>
 * - 优化缓存相关的xscript插件 <br>
 * 
 * @version 1.6.11.29 [20180510 duanyy]
 * - 增加cache相关的插件 <br>
 */
public class Cache extends Segment {
	protected String cacheId;
	protected String cid = "$cache";
	
	public Cache(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("cache",Cache.class);
		registerModule("cache-expire",CacheClear.class);
		registerModule("cache-load",CacheQuery.class);
		registerModule("cache-locate",CacheLocate.class);
		registerModule("cache-hget",CacheHashGet.class);
		registerModule("cache-hset",CacheHashSet.class);
		registerModule("cache-hdel",CacheHashDel.class);
		registerModule("cache-hexist",CacheHashExist.class);
		registerModule("cache-hsize",CacheHashSize.class);
		registerModule("cache-smembers",CacheSetMembers.class);
		registerModule("cache-sexist",CacheSetExist.class);
		registerModule("cache-sadd",CacheSetAdd.class);
		registerModule("cache-sdel",CacheSetDel.class);
		registerModule("cache-ssize",CacheSetSize.class);
	}

	@Override
	public void configure(Properties p){
		cacheId = PropertiesConstants.getString(p,"cacheId",cacheId,true);
		cid = PropertiesConstants.getString(p,"cid",cid,true);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		if (StringUtils.isEmpty(cacheId)){
			throw new BaseException("core.e1003","The relational cache is not defined");
		}
		
		Store<CacheObject> store = CacheStoreFactory.get(cacheId);
		
		if (store == null){
			throw new BaseException("core.e1003","The cache is not found,cacheId=" + cacheId);
		}
		ctx.setObject(cid, store);
		try {
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}
	}
}
