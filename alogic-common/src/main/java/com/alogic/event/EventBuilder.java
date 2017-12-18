package com.alogic.event;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 事件构造插件
 * @author yyduan
 * @since 1.6.11.2
 * 
 */
public abstract class EventBuilder extends AbstractLogiclet{
	protected String pid = "$event";
	
	public EventBuilder(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		Event e = ctx.getObject(pid);
		if (e == null){
			throw new BaseException("core.e1001",
					String.format("%s must be in an event context", getXmlTag()));
		}
		
		onExecute(e,root,current,ctx,watcher);
	}

	protected abstract void onExecute(Event e, XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher);

}