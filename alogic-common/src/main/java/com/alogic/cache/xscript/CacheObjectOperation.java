package com.alogic.cache.xscript;

import java.util.Map;

import com.alogic.cache.CacheObject;
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
 * 缓存对象操作
 * @author yyduan
 * @since 1.6.11.6
 */
public abstract class CacheObjectOperation extends AbstractLogiclet{
	protected String pid = "$cache-object";
	
	public CacheObjectOperation(String tag, Logiclet p) {
		super(tag, p);
	}
	
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid", pid);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		CacheObject cache = ctx.getObject(pid);
		if (cache == null){
			throw new BaseException("core.e1001","It must be in a cache context,check your together script.");
		}
		onExecute(cache,root,current,ctx,watcher);
	}

	@SuppressWarnings("unchecked")
	protected void onExecute(CacheObject cache, XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher){
		if (current instanceof JsonObject){
			onExecute(cache,(Map<String,Object>)root.getContent(),(Map<String,Object>)current.getContent(),ctx,watcher);
		}
	}
	
	protected abstract void onExecute(CacheObject cache, Map<String,Object> root,Map<String,Object> current, LogicletContext ctx,
			ExecuteWatcher watcher);
}