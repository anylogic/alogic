package com.alogic.remote.xscript;

import com.alogic.remote.Request;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * Request处理器父类
 * 
 * @author yyduan
 * @since 1.6.10.3
 */
public abstract class RequestHandler extends AbstractLogiclet{

	protected String pid = "remote-request";
	
	public RequestHandler(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
	}
	
	@Override
	protected void onExecute(final XsObject root,final XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		Request req = ctx.getObject(pid);
		if (req == null){
			throw new BaseException("core.e1001","It must be in a remote-request context,check your script.");
		}
		
		onExecute(req,root,current,ctx,watcher);
	}

	protected abstract void onExecute(final Request req, final XsObject root,final XsObject current, final LogicletContext ctx,
			final ExecuteWatcher watcher);	

}
