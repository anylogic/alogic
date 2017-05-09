package com.alogic.xscript.plugins;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 输出日志
 * 
 * @author duanyy
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 */
public class Log extends AbstractLogiclet{
	protected String pattern;
	protected String level;
	protected int progress = -2;
	
	public Log(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		pattern = p.GetValue("msg", "", false, true);
		level = p.GetValue("level", "info", false, true);
		progress = PropertiesConstants.getInt(p,"progress", -2);		
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		log(ctx.transform(pattern),level,progress);
	}

}
