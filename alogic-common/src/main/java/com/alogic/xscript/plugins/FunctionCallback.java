package com.alogic.xscript.plugins;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 函数回调
 * @author yyduan
 * @since 1.6.11.27
 */
public class FunctionCallback extends AbstractLogiclet {
	protected String callbackId = "$callback";
	
	public FunctionCallback(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		callbackId = PropertiesConstants.getRaw(p,"callback",callbackId);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current,LogicletContext ctx, ExecuteWatcher watcher) {
		Logiclet callback = ctx.getObject(callbackId);		
		if (callback != null){
			callback.execute(root, current, ctx, watcher);
		}
	}

}
