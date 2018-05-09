package com.alogic.cache.xscript;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.cache.CacheObject;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 请空Set
 * @author yyduan
 * @since 1.6.11.29
 */
public class CacheSetDel extends CacheObjectOperation{
	
	/**
	 * 分组
	 */
	protected String $group = CacheObject.DEFAULT_GROUP;
	
	/**
	 * 成员
	 */
	protected String $member;
	
	public CacheSetDel(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		$group = PropertiesConstants.getRaw(p, "group", $group);
		$member = PropertiesConstants.getRaw(p, "member", $member);
	}

	@Override
	protected void onExecute(CacheObject cache, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		
		String member = PropertiesConstants.transform(ctx, $member, "");
		
		if (StringUtils.isNotEmpty(member)){
			cache.sDel(
					PropertiesConstants.transform(ctx, $group, CacheObject.DEFAULT_GROUP),
					member
					);
		}else{
			cache.sDel(
					PropertiesConstants.transform(ctx, $group, CacheObject.DEFAULT_GROUP)
					);			
		}
	}

}