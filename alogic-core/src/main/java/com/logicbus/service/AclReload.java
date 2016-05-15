package com.logicbus.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.JsonTools;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.AccessController;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 重新装载AC模型
 * 
 * @author duanyy
 * 
 * @since 1.6.5.5 
 */
public class AclReload extends AbstractServant {
	
	protected void onDestroy() {
		// nothing to do
	}

	
	protected void onCreate(ServiceDescription sd){
		// nothing to do
	}

	
	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage)ctx.asMessage(XMLMessage.class);
		int offset = getArgument("offset", 0, ctx);
		int limit = getArgument("limit", 30, ctx);	
		String keyword = getArgument("keyword","",ctx);
		String acmId = getArgument("acmId","",ctx);
		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		Settings settings = Settings.get();
		AccessController ac = (AccessController) settings.get("accessController");
		if (ac != null){
			ac.reload(acmId);			
			Element acls = doc.createElement("acls");
			XmlTools.setInt(acls, "offset", offset);
			XmlTools.setInt(acls, "limit", limit);
			XmlTools.setString(acls, "keyword",keyword);
			
			ac.report(acls);
			
			root.appendChild(acls);
		}
		
		return 0;
	}

	
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		int offset = getArgument("offset", 0, ctx);
		int limit = getArgument("limit", 30, ctx);
		String keyword = getArgument("keyword","",ctx);
		String acmId = getArgument("acmId","",ctx);
		
		Map<String,Object> root = msg.getRoot();
		
		Settings settings = Settings.get();
		AccessController ac = (AccessController) settings.get("accessController");
		if (ac != null){
			ac.reload(acmId);
			Map<String,Object> acls = new HashMap<String,Object>();
			
			JsonTools.setInt(acls, "offset", offset);
			JsonTools.setInt(acls, "limit", limit);
			JsonTools.setString(acls,"keyword",keyword);

			ac.report(acls);
			
			root.put("acls", acls);
		}
		
		return 0;
	}

}