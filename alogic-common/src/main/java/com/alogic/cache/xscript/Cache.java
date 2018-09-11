package com.alogic.cache.xscript;

import org.apache.commons.lang3.StringUtils;

import com.alogic.cache.CacheObject;
import com.alogic.cache.naming.CacheStoreFactory;
import com.alogic.load.Store;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
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
 * 
 * @version 1.6.11.43 [20180708 duanyy]  <br>
 * - 增加cache-hgetall插件 <br>
 * 
 * @version 1.6.11.59 [20180911 duanyy] <br>
 * - 增加NS类，从NS上继承;
 */
public class Cache extends NS {
	protected String cacheId;
	protected String cid = "$cache";
	
	public Cache(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
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
