package com.alogic.xscript.plugins;

import java.util.Map;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;

/**
 * 进程睡眠
 * 
 * @author duanyy
 *
 */
public class Sleep extends AbstractLogiclet {
	protected String pattern;
	
	public Sleep(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		pattern = p.GetValue("timeout", "1000", false, true);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		long timeout = 0;
		try {
			timeout = Long.parseLong(ctx.transform(pattern));
		}catch (NumberFormatException ex){
			timeout = 1000;
		}
		if (timeout > 0){
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				
			}
		}
	}

}
