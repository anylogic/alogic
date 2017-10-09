package com.alogic.remote.xscript.response;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.remote.Response;
import com.alogic.remote.xscript.ResponseHandler;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 将结果作为文本输出到变量
 * 
 * @author yyduan
 * @since 1.6.10.3
 */
public class AsText extends ResponseHandler {

	protected String id = "$http-text";
	
	public AsText(String tag, Logiclet p) {
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
			try {
				ctx.SetValue(out, res.asString());
			} catch (IOException ex) {
				throw new BaseException("core.io_error",ExceptionUtils.getStackTrace(ex));
			}
		}
	}
}