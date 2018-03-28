package com.alogic.event.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Element;
import com.alogic.event.Event;
import com.alogic.event.EventProperties;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.stream.Handler;
import com.anysoft.stream.ScriptHandler;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;

/**
 * 脚本处理
 * 
 * @author yyduan
 * @version 1.6.11.26 [20180328 duanyy] <br>
 * -  支持dispatcher，用于触发进一步事件处理 <br>
 */
public class Script extends ScriptHandler<Event>{
	protected Handler<Event> dispatcher = null;
	@Override
	protected void onConfigure(Element e, Properties p) {
		super.onConfigure(e, p);
		Element dispatcherElem = XmlTools.getFirstElementByPath(e, "dispatcher");
		if (dispatcherElem != null){
			Factory<Handler<Event>> factory = new Factory<Handler<Event>>();
			dispatcher = factory.newInstance(dispatcherElem, p);
		}
	}
	
	@Override
	protected void onHandle(Event evt, long timestamp) {
		if (stmt == null){
			LOG.error("The script is null");
			return ;
		}		
		String result = "core.ok";
		String reason = "ok";
		try {
			Map<String,Object> root = new HashMap<String,Object>();
			evt.toJson(root);
			XsObject doc = new JsonObject("root",root);
			LogicletContext ctx = new LogicletContext(new EventProperties(evt,Settings.get()));
			ctx.SetValue("$task", evt.id());
			ctx.SetValue("$event", evt.getEventType());
			ctx.SetValue("$async", BooleanUtils.toStringTrueFalse(evt.isAsync()));
			
			if (dispatcher != null){
				ctx.setObject("$dispatcher", dispatcher);
			}
			
			stmt.execute(doc, doc, ctx, null);
			result = PropertiesConstants.getString(ctx, "$code", result);
			reason = PropertiesConstants.getString(ctx, "$reason", reason);
		}catch (BaseException ex){
			result = ex.getCode();
			reason = ex.getMessage();
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}finally{
			Handler<Event> dftHandler = this.getSlidingHandler();			
			if (dftHandler != null){
				evt.setProperty("$code", result, true);
				evt.setProperty("$reason", reason, true);
				dftHandler.handle(evt, timestamp);
			}
		}
	}
}
