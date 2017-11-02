package com.logicbus.kvalue.xscript.hash;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.core.HashRow;
import com.logicbus.kvalue.core.KeyValueRow;
import com.logicbus.kvalue.xscript.KVRowOperation;

public class KVHDel extends KVRowOperation {
	protected String key = "";
	protected String delimeter = "";
	
	public KVHDel(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		key = PropertiesConstants.getRaw(p,"key",key);
		delimeter = PropertiesConstants.getString(p,"delimiter",delimeter,true);
	}
	
	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String keyList = ctx.transform(key);

		if (row instanceof HashRow) {
			HashRow r = (HashRow) row;
			if (StringUtils.isNotEmpty(delimeter)){
				ctx.SetValue(id, String.valueOf(r.del(keyList.split(delimeter))));
			}else{
				ctx.SetValue(id, String.valueOf(r.del(keyList)));
			}
		}
	}

}