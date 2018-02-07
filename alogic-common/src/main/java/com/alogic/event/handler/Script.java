package com.alogic.event.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.event.Event;
import com.alogic.event.EventProperties;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.stream.ScriptHandler;
import com.anysoft.util.BaseException;
import com.anysoft.util.Settings;

/**
 * 脚本处理
 * 
 * @author yyduan
 *
 */
public class Script extends ScriptHandler<Event>{
	@Override
	protected void onHandle(Event e, long timestamp) {
		if (stmt == null){
			LOG.error("The script is null");
			return ;
		}		
		try {
			Map<String,Object> root = new HashMap<String,Object>();
			XsObject doc = new JsonObject("root",root);
			LogicletContext ctx = new LogicletContext(new EventProperties(e,Settings.get()));
			ctx.SetValue("$task", e.id());
			ctx.SetValue("$event", e.getEventType());
			ctx.SetValue("$async", BooleanUtils.toStringTrueFalse(e.isAsync()));
			stmt.execute(doc, doc, ctx, null);
		}catch (BaseException ex){
			LOG.error("Failed to execute script:" + stmt.toString());
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}
	}
}
