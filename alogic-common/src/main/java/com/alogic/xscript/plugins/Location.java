package com.alogic.xscript.plugins;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.jayway.jsonpath.JsonPath;


public class Location extends Segment {
	protected String jsonPath;
	
	public Location(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		jsonPath = PropertiesConstants.getString(p, "path", jsonPath);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		Map<String,Object> newCurrent = current;
		if (StringUtils.isNotEmpty(jsonPath)){
			Object result = JsonPath.read(current, jsonPath);
			if (result instanceof Map<?,?>){
				newCurrent = (Map<String,Object>)result;
			}else{
				logger.error("Can not locate the path:" + jsonPath);
			}
		}
		super.onExecute(root, newCurrent, ctx, watcher);
	}
}
