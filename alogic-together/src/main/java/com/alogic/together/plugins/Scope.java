package com.alogic.together.plugins;

import java.util.Map;

import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;

public class Scope extends Segment{

	public Scope(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		LogicletContext childCtx = new LogicletContext(ctx,ctx.getContext());
		super.onExecute(root, current, childCtx, watcher);
	}
	
}
