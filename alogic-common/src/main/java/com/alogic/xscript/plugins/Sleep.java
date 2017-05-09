package com.alogic.xscript.plugins;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;

/**
 * 进程睡眠
 * 
 * @author duanyy
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
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
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
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
