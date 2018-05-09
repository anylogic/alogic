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
 * 查询Set的大小
 * @author yyduan
 * @since 1.6.11.29
 */
public class CacheSetSize extends CacheObjectOperation{
	
	/**
	 * 分组
	 */
	protected String $group = CacheObject.DEFAULT_GROUP;
	
	/**
	 * 输出变量id
	 */
	protected String $id;
	
	public CacheSetSize(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$id = PropertiesConstants.getRaw(p, "id", "$" + getXmlTag());
		$group = PropertiesConstants.getRaw(p, "group", $group);
	}

	@Override
	protected void onExecute(CacheObject cache, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String id = PropertiesConstants.transform(ctx, $id, "$" + getXmlTag());
		
		if (StringUtils.isNotEmpty(id)){
			ctx.SetValue(
				id, 
				String.valueOf(
					cache.sSize(PropertiesConstants.transform(ctx, $group, CacheObject.DEFAULT_GROUP))
				)
			);
		}
	}

}
