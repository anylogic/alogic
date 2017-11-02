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

public class KVLItems extends KVRowOperation {
	protected String start = "0";
	protected String stop = "100";
	protected String tag = "data";
	
	public KVLItems(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		start = PropertiesConstants.getRaw(p,"start",start);
		stop = PropertiesConstants.getRaw(p,"stop",stop);
		tag = PropertiesConstants.getRaw(p,"tag",tag);
	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		if (row instanceof ListRow){
			ListRow r = (ListRow)row;
			String tagValue = ctx.transform(tag);
			if (StringUtils.isNotEmpty(tagValue)){
				long startPos = getLong(ctx.transform(start), 0);
				long stopPos = getLong(ctx.transform(stop),100);
				
				current.put(tagValue, r.range(startPos,stopPos));
			}
			
		}

	}

}
