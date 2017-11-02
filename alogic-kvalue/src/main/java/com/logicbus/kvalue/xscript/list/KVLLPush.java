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

public class KVLLPush extends KVRowOperation {
	protected String item = "";
	protected String delimeter = "";
	
	public KVLLPush(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		item = PropertiesConstants.getRaw(p,"item",item);
		delimeter = PropertiesConstants.getString(p,"delimeter",delimeter,true);		
	}
	
	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String items = ctx.transform(item);

		if (row instanceof ListRow) {
			ListRow r = (ListRow) row;
			if (StringUtils.isNotEmpty(delimeter)){
				ctx.SetValue(id, String.valueOf(r.leftPush(items.split(delimeter))));
			}else{
				ctx.SetValue(id, String.valueOf(r.leftPush(items)));
			}
		}
	}

}
