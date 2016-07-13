package com.alogic.xscript.plugins;

import java.util.Map;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.PropertiesConstants;

/**
 * Helloworld
 * 
 * @author duanyy
 *
 */
public class Helloworld extends AbstractLogiclet {
	
	public Helloworld(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		String who = PropertiesConstants.getString(ctx,"who","nobody");
		
		if (current != null){
			current.put("message", "hello " + who);
		}
	}

}
