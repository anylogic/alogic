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

public class KVHMSet extends KVRowOperation {
	protected String values = "";
	protected String delimeter = ";";
	
	public KVHMSet(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		values = PropertiesConstants.getRaw(p,"values",values);
		delimeter = PropertiesConstants.getString(p,"delimiter",delimeter,true);
	}
	
	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String vals = ctx.transform(values);

		if (row instanceof HashRow) {
			HashRow r = (HashRow) row;
			ctx.SetValue(id, String.valueOf(r.mset(vals.split(delimeter))));
		}
	}

}