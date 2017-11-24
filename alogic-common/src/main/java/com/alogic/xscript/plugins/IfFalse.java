package com.alogic.xscript.plugins;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 如果取值为false，则执行
 * 
 * @author yyduan
 *
 * @since 1.6.10.9
 */
public class IfFalse extends Segment{
	protected String pattern = "false";
	
	public IfFalse(String tag, Logiclet p) {
		super(tag, p);
	}	
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		pattern = PropertiesConstants.getRaw(p,"value",pattern);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		boolean value = PropertiesConstants.transform(ctx, pattern, false);
		if (!value){
			super.onExecute(root, current, ctx, watcher);
		}
	}	
}