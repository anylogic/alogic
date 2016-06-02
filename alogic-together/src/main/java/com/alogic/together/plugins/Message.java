package com.alogic.together.plugins;

import java.util.Map;

import com.alogic.together.AbstractLogiclet;
import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * Message
 * 
 * @author duanyy
 *
 */
public class Message extends AbstractLogiclet {
	protected String msg = "hello";
	
	public Message(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		msg = PropertiesConstants.getString(p,"msg",msg);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (current != null){
			current.put("message", msg);
		}
	}

}
