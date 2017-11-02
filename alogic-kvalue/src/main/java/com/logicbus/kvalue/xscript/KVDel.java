package com.logicbus.kvalue.xscript;

import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.logicbus.kvalue.core.KeyValueRow;

/**
 * 删除当前ROW
 * 
 * @author duanyy
 *
 */
public class KVDel extends KVRowOperation {

	public KVDel(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		ctx.SetValue(id, Boolean.toString(row.delete()));
	}

}
