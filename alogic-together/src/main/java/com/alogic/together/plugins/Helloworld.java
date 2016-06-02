package com.alogic.together.plugins;

import java.util.Map;

import com.alogic.together.AbstractLogiclet;
import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
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
