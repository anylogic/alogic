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
 * 新增缓存对象
 * 
 * @author yyduan
 * @since 1.6.11.60 [20180912 duanyy]
 */
public class CacheNew extends NS {
	
	/**
	 * 缓存id
	 */
	protected String id = "id";
	
	/**
	 * 父节点的上下文id
	 */
	protected String pid = "$cache";
	
	/**
	 * 当前节点的上下文id
	 */
	protected String cid = "$cache-object";
	
	protected String cacheId;
	
	/**
	 * 结果代码
	 */
	protected String result = "$result";
	
	public CacheNew(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		id = PropertiesConstants.getRaw(p, "id", "");		
		pid = PropertiesConstants.getString(p,"pid", pid,true);
		cid = PropertiesConstants.getString(p,"cid", cid,true);
		cacheId = PropertiesConstants.getString(p,"cacheId", "",true);
		result = PropertiesConstants.getString(p, "result", "$" + this.getXmlTag(),true);
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
		
		String idValue = ctx.transform(id);
		if (StringUtils.isNotEmpty(idValue)){
			CacheObject found = cache.load(idValue,true);
			if (found != null){
				try {
					ctx.SetValue(result, "false");
					ctx.setObject(cid, found);
					super.onExecute(root, current, ctx, watcher);
				}finally{
					ctx.removeObject(cid);
				}
			}else{				
				found = new CacheObject.Simple(idValue);				
				try {
					ctx.SetValue(result, "true");
					ctx.setObject(cid, found);
					super.onExecute(root, current, ctx, watcher);
					cache.save(idValue, found, true);
				}finally{
					ctx.removeObject(cid);
				}
			}
		}else{
			ctx.SetValue(result, "false");
		}
	}

}
