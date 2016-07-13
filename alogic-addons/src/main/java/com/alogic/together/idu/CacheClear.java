package com.alogic.together.idu;

import java.util.Map;

import com.alogic.cache.core.CacheStore;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

public class CacheClear extends CacheOperation{
	protected String id = "id";
	
	public CacheClear(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		id = PropertiesConstants.getString(p, "id", id);
	}

	@Override
	protected void onExecute(CacheStore cache, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String idValue = getArgument(id,ctx);
		cache.expire(idValue);
	}

}
