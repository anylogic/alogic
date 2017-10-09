package com.alogic.remote.xscript.request;

import org.apache.commons.lang3.StringUtils;

import com.alogic.remote.Request;
import com.alogic.remote.xscript.RequestHandler;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 通过一般的文本发送消息体
 * 
 * @author yyduan
 * @since 1.6.10.3
 */
public class ByText extends RequestHandler {

	protected String value = "";
	
	public ByText(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		value = PropertiesConstants.getRaw(p,"value",value);
	}
	
	@Override
	protected void onExecute(final Request req, final XsObject root,final XsObject current, final LogicletContext ctx,
			final ExecuteWatcher watcher) {
		String body = ctx.transform(value);
		if (StringUtils.isNotEmpty(body)){
			req.setBody(body);
		}
	}
}