package com.logicbus.kvalue.xscript.list;

import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.logicbus.kvalue.xscript.KVRowOperation;
import com.logicbus.kvalue.core.KeyValueRow;
import com.logicbus.kvalue.core.ListRow;

public class KVLLength extends KVRowOperation {

	public KVLLength(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		if (row instanceof ListRow){
			ListRow r = (ListRow)row;
			ctx.SetValue(id, String.valueOf(r.length()));
		}
	}

}
