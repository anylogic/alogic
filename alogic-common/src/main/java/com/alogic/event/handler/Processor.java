package com.alogic.event.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Element;

import com.alogic.event.Event;
import com.alogic.event.EventProperties;
import com.alogic.load.Loader;
import com.anysoft.stream.Handler;
import com.anysoft.stream.SlideHandler;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.alogic.event.Process;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;

/**
 * 事件处理器
 * @author yyduan
 *
 */
public class Processor extends SlideHandler<Event>{
	
	protected Loader<Process> loader = null;
	
	@Override
	protected void onHandle(Event e, long timestamp) {
		Process p = getProcess(e.getEventType());
		if (p != null){
			//根据事件类型找到了相应的处理
			Script script = p.getScript();
			if (script == null){
				LOG.error(String.format("Can not execute process %s ,because the script is null.",p.getId()));
			}else{
				String result = "core.ok";
				String reason = "ok";
				try {
					Map<String,Object> root = new HashMap<String,Object>();
					XsObject doc = new JsonObject("root",root);
					LogicletContext ctx = new LogicletContext(new EventProperties(e,Settings.get()));
					ctx.SetValue("$task", e.id());
					ctx.SetValue("$event", e.getEventType());
					script.execute(doc, doc, ctx, null);
					result = PropertiesConstants.getString(ctx, "$code", result);
					reason = PropertiesConstants.getString(ctx, "$reason", reason);
				}catch (BaseException ex){
					result = ex.getCode();
					reason = ex.getMessage();
					LOG.error("Failed to execute process:" + p.getId());
					LOG.error(ExceptionUtils.getStackTrace(ex));
				}
				Handler<Event> dftHandler = this.getSlidingHandler();			
				if (dftHandler != null){
					e.setProperty("$code", result, true);
					e.setProperty("$reason", reason, true);
					dftHandler.handle(e, timestamp);
				}
			}
		}else{
			LOG.warn("Can not find process for " + e.getEventType());
			LOG.warn(e.toString());
		}
	}

	protected Process getProcess(String id){
		return loader == null ? null : loader.load(id, true);
	}

	@Override
	protected void onConfigure(Element e, Properties p) {
		super.onConfigure(e, p);
		
		Element elem = XmlTools.getFirstElementByPath(e, "loader");
		if (elem != null){
			Factory<Loader<Process>> f = new Factory<Loader<Process>>();			
			try {
				loader = f.newInstance(elem, p, "module");
			}catch (Exception ex){
				LOG.error("Can not create loader with " + XmlTools.node2String(elem));
				LOG.error(ExceptionUtils.getStackTrace(ex));
			}
		}
	}

}
