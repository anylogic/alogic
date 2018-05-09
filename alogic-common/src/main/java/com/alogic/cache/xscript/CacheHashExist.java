package com.alogic.cache.xscript;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.alogic.cache.CacheObject;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * Cache的hash中是否存在指定的key
 * 
 * @author yyduan
 * @since 1.6.11.29
 */
public class CacheHashExist extends CacheObjectOperation{
	
	/**
	 * 分组
	 */
	protected String $group = CacheObject.DEFAULT_GROUP;
	
	/**
	 * 输出变量id
	 */
	protected String $id;
	
	/**
	 * 成员
	 */
	protected String $key;
	
	public CacheHashExist(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$id = PropertiesConstants.getRaw(p, "id", "$" + getXmlTag());
		$group = PropertiesConstants.getRaw(p, "group", $group);
		$key = PropertiesConstants.getRaw(p, "key", $key);
	}

	@Override
	protected void onExecute(CacheObject cache, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String id = PropertiesConstants.transform(ctx, $id, "$" + getXmlTag());
		
		if (StringUtils.isNotEmpty(id)){
			ctx.SetValue(id, BooleanUtils.toStringTrueFalse(cache.hExist(
					PropertiesConstants.transform(ctx, $group, CacheObject.DEFAULT_GROUP), 
					PropertiesConstants.transform(ctx, $key, ""))));
		}
	}

}