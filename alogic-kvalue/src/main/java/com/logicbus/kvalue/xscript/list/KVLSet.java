package com.logicbus.kvalue.xscript.list;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.logicbus.kvalue.xscript.KVRowOperation;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.core.KeyValueRow;
import com.logicbus.kvalue.core.ListRow;

public class KVLSet extends KVRowOperation {
	protected String index = "0";
	protected String value = "";
	
	public KVLSet(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		index = PropertiesConstants.getRaw(p,"index",index);
		value = PropertiesConstants.getRaw(p,"value",value);
	}
	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		if (row instanceof ListRow){
			ListRow r = (ListRow)row;
			
			long offset = getLong(ctx.transform(index),0);
			String v = ctx.transform(value);
			
			if (StringUtils.isNotEmpty(v)){
				ctx.SetValue(id, String.valueOf(r.set(offset, v)));
			}
		}

	}

}
