package com.logicbus.kvalue.xscript;

import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.logicbus.kvalue.core.KeyValueRow;

/**
 * 当前ROW是否存在，将结果存入指定变量
 * 
 * @author duanyy
 *
 */
public class KVExist extends KVRowOperation {
	public KVExist(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		ctx.SetValue(id, Boolean.toString(row.exists()));
	}
}
