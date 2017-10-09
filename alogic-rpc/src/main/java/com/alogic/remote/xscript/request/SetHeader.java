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
 * 设置请求头
 * 
 * @author yyduan
 * @since 1.6.10.3
 */
public class SetHeader extends RequestHandler {

	protected String id = "";
	protected String value = "";
	protected String dft = "";
	
	public SetHeader(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getRaw(p,"header",id);
		value = PropertiesConstants.getRaw(p,"value",value);
		dft = PropertiesConstants.getRaw(p,"dft",dft);
	}
	
	@Override
	protected void onExecute(final Request req, final XsObject root,final XsObject current, final LogicletContext ctx,
			final ExecuteWatcher watcher) {
		String header = ctx.transform(id);
		if (StringUtils.isNotEmpty(header)){
			String data = ctx.transform(value);
			data = StringUtils.isEmpty(data) ? ctx.transform(dft) : data;
			
			if (StringUtils.isNotEmpty(data)){
				req.setHeader(id, data);
			}
		}
	}

}
