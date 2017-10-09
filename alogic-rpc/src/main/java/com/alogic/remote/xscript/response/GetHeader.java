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
 * 获取相应头数据
 * 
 * @author yyduan
 * @since 1.6.10.3
 */
public class GetHeader extends ResponseHandler {

	protected String id = "";
	protected String header = "";
	protected String dft = "";
	
	public GetHeader(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getRaw(p,"id",id);
		header = PropertiesConstants.getRaw(p,"header",header);
		dft = PropertiesConstants.getRaw(p,"dft",dft);
	}
	
	@Override
	protected void onExecute(final Response res,
			final XsObject root,final XsObject current, final LogicletContext ctx,
			final ExecuteWatcher watcher) {
		String out = ctx.transform(id);
		if (StringUtils.isNotEmpty(out)){
			String name = ctx.transform(header);
			String data = res.getHeader(name,  ctx.transform(dft));
			if (StringUtils.isNotEmpty(data)){
				ctx.SetValue(out, data);
			}
		}
	}

}
