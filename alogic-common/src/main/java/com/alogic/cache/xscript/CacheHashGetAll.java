package com.alogic.cache.xscript;

import java.util.List;
import com.alogic.cache.CacheObject;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Pair;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 获取指定group中所有hash值
 * @author yyduan
 * @since 1.6.11.43
 */
public class CacheHashGetAll extends Segment{
	protected String pid = "$cache-object";	
	/**
	 * 分组
	 */
	protected String $group = CacheObject.DEFAULT_GROUP;
	
	/**
	 * 输出keyid
	 */
	protected String key = "$key";
	
	/**
	 * 输出值id
	 */
	protected String value = "$value";
	
	/**
	 * 缺省值
	 */
	protected String dft = "";
	
	/**
	 * 条件
	 */
	protected String condition = "*";	
	
	public CacheHashGetAll(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid", pid,true);
		key = PropertiesConstants.getString(p, "key", key,true);
		value = PropertiesConstants.getString(p, "value", value,true);
		$group = PropertiesConstants.getRaw(p, "group", $group);
		condition = PropertiesConstants.getString(p,"condition", condition,true);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		CacheObject cache = ctx.getObject(pid);
		if (cache == null){
			throw new BaseException("core.e1001","It must be in a cache context,check your together script.");
		}
		
		String group = PropertiesConstants.transform(ctx,$group,CacheObject.DEFAULT_GROUP);
		
		List<Pair<String,String>> result = cache.hGetAll(group, condition);
		
		for (Pair<String,String> pair:result){
			ctx.SetValue(key, pair.key());
			ctx.SetValue(value, pair.value());
			super.onExecute(root, current, ctx, watcher);
		}
	}	

}
