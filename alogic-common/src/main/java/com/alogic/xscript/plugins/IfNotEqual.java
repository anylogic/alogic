package com.alogic.xscript.plugins;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 如果两个取值不相等，则执行
 * 
 * @author yyduan
 *
 */
public class IfNotEqual extends Segment{
	protected String $left = "true";
	protected String $right = "true";
	protected boolean ignoreCase = false;
	
	public IfNotEqual(String tag, Logiclet p) {
		super(tag, p);
	}	
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		$left = PropertiesConstants.getRaw(p,"left",$left);
		$right = PropertiesConstants.getRaw(p,"right",$right);
		ignoreCase = PropertiesConstants.getBoolean(p, "ignoreCase", ignoreCase);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String left = PropertiesConstants.transform(ctx, $left, "true");
		String right = PropertiesConstants.transform(ctx, $right, "true");
		
		if (ignoreCase){
			if (!left.equalsIgnoreCase(right)){
				super.onExecute(root, current, ctx, watcher);
			}
		}else{
			if (!left.equals(right)){
				super.onExecute(root, current, ctx, watcher);
			}
		}
	}	
}
