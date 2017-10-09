package com.alogic.remote.xscript.response;

import com.alogic.remote.Response;
import com.alogic.remote.xscript.ResponseHandler;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;

/**
 * 抛弃调用结果
 * 
 * @author yyduan
 * @since 1.6.10.3
 */
public class Discard extends ResponseHandler {

	public Discard(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
	}
	
	@Override
	protected void onExecute(final Response res,
			final XsObject root,final XsObject current, final LogicletContext ctx,
			final ExecuteWatcher watcher) {
		res.discard();
	}
}