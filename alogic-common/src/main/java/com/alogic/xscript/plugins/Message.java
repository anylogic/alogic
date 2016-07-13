package com.alogic.xscript.plugins;

import java.util.Map;

import com.alogic.xscript.util.MapProperties;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
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
			MapProperties p = new MapProperties(current,ctx);
			current.put("msg", p.transform(msg));
		}
	}

}
