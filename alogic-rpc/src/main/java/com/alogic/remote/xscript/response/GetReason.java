package com.alogic.remote.xscript.response;

import org.apache.commons.lang3.StringUtils;

import com.alogic.remote.Response;
import com.alogic.remote.xscript.ResponseHandler;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 获取结果原因
 * 
 * @author yyduan
 * @since 1.6.10.3
 */
public class GetReason extends ResponseHandler {

	protected String id = "$http-reason";
	
	public GetReason(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getRaw(p,"id",id);
	}
	
	@Override
	protected void onExecute(final Response res,
			final XsObject root,final XsObject current, final LogicletContext ctx,
			final ExecuteWatcher watcher) {
		String out = ctx.transform(id);
		if (StringUtils.isNotEmpty(out)){
			ctx.SetValue(out, String.valueOf(res.getReasonPhrase()));
		}
	}
}