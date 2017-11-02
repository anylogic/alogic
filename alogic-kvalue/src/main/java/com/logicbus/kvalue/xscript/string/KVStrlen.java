package com.logicbus.kvalue.xscript.string;

import java.util.Map;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.logicbus.kvalue.xscript.KVRowOperation;
import com.logicbus.kvalue.core.KeyValueRow;
import com.logicbus.kvalue.core.StringRow;

/**
 * 统计当前字符串的长度，相当于redis指令:strlen
 * 
 * @author duanyy
 *
 */
public class KVStrlen extends KVRowOperation {
	public KVStrlen(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		if (row instanceof StringRow){
			StringRow r = (StringRow)row;
			
			ctx.SetValue(id, String.valueOf(r.strlen()));
		}
	}

}
