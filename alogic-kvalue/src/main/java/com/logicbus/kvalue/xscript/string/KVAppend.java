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
 * append值到当前值，对应redis指令:append
 * 
 * @author duanyy
 *
 */
public class KVAppend extends KVRowOperation {
	protected String value = "";

	public KVAppend(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		value = PropertiesConstants.getRaw(p,"value",value);
	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		if (row instanceof StringRow){
			StringRow r = (StringRow)row;
			
			String v = ctx.transform(value);
			if (StringUtils.isNotEmpty(v)){
				ctx.SetValue(id,String.valueOf(r.append(v)));
			}
		}
	}

}
