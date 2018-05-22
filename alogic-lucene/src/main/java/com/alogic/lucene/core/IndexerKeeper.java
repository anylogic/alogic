package com.alogic.lucene.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Element;

import com.alogic.event.Event;
import com.alogic.event.EventProperties;
import com.alogic.event.EventServer;
import com.alogic.lucene.client.IndexerTool;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;

/**
 * Indexer维护者
 * 
 * @author yyduan
 * @since 1.6.11.31
 */
public class IndexerKeeper extends EventServer.Abstract{
	
	/**
	 * indexer id
	 */
	protected String indexerId = "default";
	
	protected Script stmt = null;
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		indexerId = PropertiesConstants.getString(p,"indexer",indexerId);
	}
	
	@Override
	public void configure(Element e, Properties p) {
		super.configure(e, p);
		
		Element script = XmlTools.getFirstElementByPath(e, "script");
		if (script != null){
			stmt = Script.create(script, p);
		}
	}
	
	@Override
	public void start() {
		Indexer indexer = IndexerTool.getIndexer(indexerId);
		if (indexer != null){
			indexer.build(true);
		}else{
			LOG.error("Can not find lucene indexer:" + indexerId);
		}
	}

	@Override
	public void stop() {
		// nothing to do
	}

	@Override
	public void join(long timeout) {
		// nothing to do
	}

	@Override
	public void handle(Event evt, long timestamp) {
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
			stmt.execute(doc, doc, ctx, null);
			result = PropertiesConstants.getString(ctx, "$code", result);
			reason = PropertiesConstants.getString(ctx, "$reason", reason);
		}catch (BaseException ex){
			result = ex.getCode();
			reason = ex.getMessage();
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}finally{
			evt.setProperty("$code", result, true);
			evt.setProperty("$reason", reason, true);
		}
	}
}
