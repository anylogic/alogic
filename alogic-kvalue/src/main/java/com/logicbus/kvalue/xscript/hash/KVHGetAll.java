package com.logicbus.kvalue.xscript.hash;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.logicbus.kvalue.xscript.KVRowOperation;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.core.HashRow;
import com.logicbus.kvalue.core.KeyValueRow;

public class KVHGetAll extends KVRowOperation {
	protected String tag = "data";
	protected boolean extend = false;
	
	public KVHGetAll(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		tag = PropertiesConstants.getRaw(p,"tag",tag);
		extend = PropertiesConstants.getBoolean(p,"extend",extend);
	}
	
	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		if (extend){
			if (row instanceof HashRow) {
				HashRow r = (HashRow) row;
				r.getAll(current);
			}
		}else{
			String tagValue = ctx.transform(tag);
			if (StringUtils.isNotEmpty(tagValue) && row instanceof HashRow) {
				HashRow r = (HashRow) row;
				current.put(tagValue,r.getAll(null));
			}			
		}
	}

}