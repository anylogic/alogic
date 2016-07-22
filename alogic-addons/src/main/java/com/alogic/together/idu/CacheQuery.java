package com.alogic.together.idu;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.cache.core.CacheStore;
import com.alogic.cache.core.MultiFieldObject;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.ServantException;

public class CacheQuery extends CacheOperation {
	protected String tag = "data";
	protected String id = "id";
	
	public CacheQuery(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		tag = PropertiesConstants.getRaw(p, "tag", tag);
		id = PropertiesConstants.getRaw(p, "id", "");		
	}	
	
	@Override
	protected void onExecute(CacheStore cache, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String idValue = ctx.transform(id);
		if (StringUtils.isNotEmpty(idValue)){
			MultiFieldObject found = cache.get(idValue, true);
			if (found == null){
				throw new ServantException("core.data_not_found","Can not find object,id=" + idValue);
			}
		
			Map<String,Object> data = new HashMap<String,Object>();		
			found.toJson(data);		
			String tagValue = ctx.transform(tag);
			current.put(tagValue, data);
		}
	}

}
