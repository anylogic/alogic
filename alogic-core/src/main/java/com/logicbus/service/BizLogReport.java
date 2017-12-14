package com.logicbus.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.JsonTools;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.bizlog.BizLogger;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * BizLog报告
 * 
 * @author duanyy
 *
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 将MessageDoc和Context进行合并整合 <br>
 * 
 * @version 1.6.4.44 [20160414 duanyy] <br>
 * - 增加统计数据的分页功能 <br>
 */
public class BizLogReport extends AbstractServant{

	
	protected void onDestroy() {
	}

	
	protected void onCreate(ServiceDescription sd) throws ServantException{
	}

	
	protected int onXml(Context ctx) {
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);
		int offset = getArgument("offset", 0, ctx);
		int limit = getArgument("limit", 30, ctx);	
		String keyword = getArgument("keyword","",ctx);
		
		Settings settings = Settings.get();
		
		BizLogger bizLogger = (BizLogger) settings.get("bizLogger");
		if (bizLogger != null){
			Element root = msg.getRoot();
			Document doc = root.getOwnerDocument();
			
			Element logger = doc.createElement(bizLogger.getHandlerType());
			XmlTools.setInt(logger, "offset", offset);
			XmlTools.setInt(logger, "limit", limit);
			XmlTools.setString(logger, "keyword",keyword);
			
			bizLogger.report(logger);
			
			root.appendChild(logger);
		}
		return 0;
	}

	
	protected int onJson(Context ctx) {
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		int offset = getArgument("offset", 0, ctx);
		int limit = getArgument("limit", 30, ctx);	
		String keyword = getArgument("keyword","",ctx);
		
		Settings settings = Settings.get();
		
		BizLogger bizLogger = (BizLogger) settings.get("bizLogger");
		if (bizLogger != null){
			Map<String,Object> root = msg.getRoot();
		
			Map<String,Object> logger = new HashMap<String,Object>();
			JsonTools.setInt(logger, "offset", offset);
			JsonTools.setInt(logger, "limit", limit);
			JsonTools.setString(logger, "keyword",keyword);			
			bizLogger.report(logger);
			
			root.put(bizLogger.getHandlerType(), logger);
		}
		return 0;
	}


}
