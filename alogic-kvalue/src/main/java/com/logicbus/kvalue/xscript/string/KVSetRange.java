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
 * 覆盖当前字符串的一定范围，类似于redis指令:setrange
 * @author duanyy
 *
 */
public class KVSetRange extends KVRowOperation {
	protected String value = "";
	protected String offset = "0";
	
	public KVSetRange(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		value = PropertiesConstants.getRaw(p,"value",value);
		offset = PropertiesConstants.getRaw(p,"offset",offset);
	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		if (row instanceof StringRow){
			StringRow r = (StringRow)row;
			
			String v = ctx.transform(value);
			if (StringUtils.isNotEmpty(v)){
				long index = getLong(ctx.transform(offset),0);
				ctx.SetValue(id, String.valueOf(r.setRange(index, v)));
			}
		}
	}

}
