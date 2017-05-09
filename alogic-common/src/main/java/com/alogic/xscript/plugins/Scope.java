package com.alogic.xscript.plugins;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;

/**
 * Scope
 * @author yyduan
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 */
public class Scope extends Segment{

	public Scope(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		LogicletContext childCtx = new LogicletContext(ctx);
		super.onExecute(root, current, childCtx, watcher);
	}
	
}
