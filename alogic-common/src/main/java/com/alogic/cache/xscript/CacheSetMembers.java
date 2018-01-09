package com.alogic.cache.xscript;

import java.util.List;
import com.alogic.cache.CacheObject;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 获取Set的成员
 * 
 * @author yyduan
 * @since 1.6.11.8 
 */
public class CacheSetMembers extends Segment{
	protected String pid = "$cache-object";
	
	/**
	 * 分组
	 */
	protected String $group = CacheObject.DEFAULT_GROUP;
	
	/**
	 * 输出变量id
	 */
	protected String $id = "$value";
	
	/**
	 * 条件
	 */
	protected String condition = "*";
	
	public CacheSetMembers(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid", pid,true);
		$id = PropertiesConstants.getRaw(p, "id", $id);
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
		
		String idValue = PropertiesConstants.transform(ctx,$id,"$value");
		String group = PropertiesConstants.transform(ctx,$group,CacheObject.DEFAULT_GROUP);
		
		List<String> members = cache.sMembers(group, condition);
		
		for (String mem:members){
			ctx.SetValue(idValue, mem);
			super.onExecute(root, current, ctx, watcher);
		}
	}	
}
