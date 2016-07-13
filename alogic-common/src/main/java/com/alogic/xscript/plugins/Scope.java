package com.alogic.xscript.plugins;

import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;

public class Scope extends Segment{

	public Scope(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		LogicletContext childCtx = new LogicletContext(ctx);
		super.onExecute(root, current, childCtx, watcher);
	}
	
}
