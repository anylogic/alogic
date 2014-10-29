package com.logicbus.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.MessageDoc;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;
import com.logicbus.remote.context.CallSource;
import com.logicbus.remote.core.Call;

public class RemoteCallReport extends AbstractServant {

	
	protected void onDestroy() {
	}

	
	protected void onCreate(ServiceDescription sd) throws ServantException {
	}

	
	protected int onXml(MessageDoc msgDoc, Context ctx) throws Exception {
		XMLMessage msg = (XMLMessage)msgDoc.asMessage(XMLMessage.class);
		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		String id = getArgument("id","all", msgDoc, ctx);	
		if (id == null || id.length() <= 0 || id.equals("all")){
			Element _source = doc.createElement("source");
			
			CallSource source = CallSource.get();				
			source.report(_source);
			
			root.appendChild(_source);
		}else{			
			Element _call = doc.createElement("call");
			
			CallSource source = CallSource.get();				
			Call call = source.get(id);		
			if (call != null){
				call.report(_call);
			}
			root.appendChild(_call);
		}
		return 0;
	}

	
	protected int onJson(MessageDoc msgDoc, Context ctx) throws Exception {
		JsonMessage msg = (JsonMessage) msgDoc.asMessage(JsonMessage.class);
		Map<String,Object> root = msg.getRoot();

		String id = getArgument("id","all", msgDoc, ctx);	
		
		if (id == null || id.length() <= 0 || id.equals("all")){
			Map<String,Object> _source = new HashMap<String,Object>();
			
			CallSource source = CallSource.get();				
			source.report(_source);
			
			root.put("source", _source);
		}else{
			Map<String,Object> _call = new HashMap<String,Object>();
			
			CallSource source = CallSource.get();					
			Call call = source.get(id);		
			if (call != null){
				call.report(_call);
			}
			root.put("call", _call);
		}
		return 0;
	}

}
