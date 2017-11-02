package com.logicbus.kvalue.xscript.string;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.logicbus.kvalue.xscript.KVRowOperation;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.core.KeyValueRow;
import com.logicbus.kvalue.core.StringRow;

/**
 * 设置一个string值，对应redis指令:set
 * 
 * @author duanyy
 *
 */
public class KVSet extends KVRowOperation {
	protected String value = "";
	protected long ttl = 0;
	protected boolean writeIfExist = false;
	protected boolean writeIfNotExist = false;
	
	public KVSet(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		value = PropertiesConstants.getRaw(p,"value",value);
		ttl = PropertiesConstants.getLong(p,"ttl",ttl,true);
		writeIfExist = PropertiesConstants.getBoolean(p,"writeIfExist",writeIfExist,true);
		writeIfNotExist = PropertiesConstants.getBoolean(p,"writeIfNotExist",writeIfNotExist,true);
	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		if (row instanceof StringRow){
			StringRow r = (StringRow)row;
			
			String v = ctx.transform(value);
			if (StringUtils.isNotEmpty(v)){
				if (ttl > 0){
					ctx.SetValue(id,String.valueOf(r.set(v, ttl, writeIfExist, writeIfNotExist)));
				}else{
					ctx.SetValue(id,String.valueOf(r.set(v)));
				}
			}
		}
	}

}
