package com.logicbus.kvalue.xscript.hash;

import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.logicbus.kvalue.xscript.KVRowOperation;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.core.HashRow;
import com.logicbus.kvalue.core.KeyValueRow;

public class KVHSet extends KVRowOperation {
	protected String key = "";
	protected String value = "";
	
	public KVHSet(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		key = PropertiesConstants.getRaw(p,"key",key);
		value = PropertiesConstants.getRaw(p,"value",value);
	}
	
	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String field = ctx.transform(key);

		if (row instanceof HashRow) {
			HashRow r = (HashRow) row;
			String v = ctx.transform(value);
			ctx.SetValue(id, String.valueOf(r.set(field, v)));
		}
	}

}