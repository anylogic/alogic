package com.alogic.cache.xscript;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.cache.CacheObject;
import com.alogic.cache.naming.CacheStoreFactory;
import com.alogic.load.Store;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 针对缓存的操作
 * 
 * @author duanyy
 * 
 * @since 1.6.10.5
 * 
 * @version 1.6.11.6 [20180103 duanyy] <br>
 * - 从alogic-cache中迁移过来
 * 
 * @version 1.6.11.8 [20180109] duanyy <br>
 * - 优化缓存相关的xscript插件 <br>
 * 
 * @version 1.6.11.9 [20180111] duanyy <br>
 * - 优化缓存相关的xscript插件 <br>
 */
public abstract class CacheOperation extends AbstractLogiclet{
	protected String pid = "$cache";
	protected String cacheId;
	public CacheOperation(String tag, Logiclet p) {
		super(tag, p);
	}
	
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid", pid,true);
		cacheId = PropertiesConstants.getString(p,"cacheId", "",true);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		Store<CacheObject> cache = ctx.getObject(pid);
		if (cache == null){
			if (StringUtils.isNotEmpty(cacheId)){
				cache = CacheStoreFactory.get(cacheId);
			}
			
			if (cache == null){
				throw new BaseException("core.e1001","It must be in a cache context,check your together script.");
			}
		}
		onExecute(cache,root,current,ctx,watcher);
	}

	@SuppressWarnings("unchecked")
	protected void onExecute(Store<CacheObject> cache, XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher){
		if (current instanceof JsonObject){
			onExecute(cache,(Map<String,Object>)root.getContent(),(Map<String,Object>)current.getContent(),ctx,watcher);
		}
	}
	
	protected void onExecute(Store<CacheObject> cache, Map<String,Object> root,Map<String,Object> current, LogicletContext ctx,
			ExecuteWatcher watcher){
		throw new BaseException("core.e1000",
				String.format("Tag %s does not support protocol %s",this.getXmlTag(),root.getClass().getName()));
	}
}