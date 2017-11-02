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

public class KVHKeys extends KVRowOperation {
	protected String tag = "data";
	
	public KVHKeys(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		tag = PropertiesConstants.getRaw(p,"tag",tag);
	}
	
	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String tagValue = ctx.transform(tag);

		if (StringUtils.isNotEmpty(tagValue) && row instanceof HashRow) {
			HashRow r = (HashRow) row;
			current.put(tagValue, r.keys());
		}
	}

}