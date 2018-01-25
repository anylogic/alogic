package com.alogic.together.idu;

import org.apache.commons.lang3.StringUtils;

import com.alogic.cache.CacheObject;
import com.alogic.cache.naming.CacheStoreFactory;
import com.alogic.load.Store;
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
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.11.13 [20180125 duanyy] <br>
 * - 切换到新的缓存实现 <br>
 * 
 * @deprecated
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
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		if (StringUtils.isEmpty(cacheId)){
			throw new ServantException("core.e1003","The relational cache is not defined");
		}
		
		Store<CacheObject> store = CacheStoreFactory.get(cacheId);
		if (store == null){
			throw new ServantException("clnt.e2007","Can not find a cache :" + cacheId);
		}
		ctx.setObject(cacheConn, store);
		try {
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cacheConn);
		}
	}
}
