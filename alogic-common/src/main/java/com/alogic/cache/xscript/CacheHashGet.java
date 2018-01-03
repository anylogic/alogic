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
 * 从缓存中获取Hash值
 * @author yyduan
 * @since 1.6.11.6
 */
public class CacheHashGet extends CacheObjectOperation{
	
	/**
	 * 分组
	 */
	protected String group = CacheObject.DEFAULT_GROUP;
	
	/**
	 * 输出变量id
	 */
	protected String id;
	
	/**
	 * hash key
	 */
	protected String key;
	
	/**
	 * 缺省值
	 */
	protected String dft = "";
	
	public CacheHashGet(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getRaw(p, "id", "");
		group = PropertiesConstants.getRaw(p, "group", group);
		key =  PropertiesConstants.getRaw(p, "key", id);
		dft =  PropertiesConstants.getRaw(p, "dft", dft);
	}

	@Override
	protected void onExecute(CacheObject cache, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String idValue = PropertiesConstants.transform(ctx, id, "");
		
		if (StringUtils.isNotEmpty(idValue)){
			ctx.SetValue(idValue, cache.hGet(
					PropertiesConstants.transform(ctx, group, CacheObject.DEFAULT_GROUP), 
					PropertiesConstants.transform(ctx, key, ""), 
					PropertiesConstants.transform(ctx, dft, "")
					));
		}
	}

}
