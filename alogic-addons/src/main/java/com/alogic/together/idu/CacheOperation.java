package com.alogic.together.idu;

import java.util.Map;

import com.alogic.cache.core.CacheStore;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.ServantException;

/**
 * 针对缓存的操作
 * 
 * @author duanyy
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
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
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		CacheStore cache = ctx.getObject(cacheConn);
		if (cache == null){
			throw new ServantException("core.e1001","It must be in a cache context,check your together script.");
		}
		onExecute(cache,root,current,ctx,watcher);
	}

	@SuppressWarnings("unchecked")
	protected void onExecute(CacheStore cache, XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher){
		if (current instanceof JsonObject){
			onExecute(cache,(Map<String,Object>)root.getContent(),(Map<String,Object>)current.getContent(),ctx,watcher);
		}
	}
	
	protected void onExecute(CacheStore cache, Map<String,Object> root,Map<String,Object> current, LogicletContext ctx,
			ExecuteWatcher watcher){
		throw new BaseException("core.e1000",
				String.format("Tag %s does not support protocol %s",this.getXmlTag(),root.getClass().getName()));
	}
}