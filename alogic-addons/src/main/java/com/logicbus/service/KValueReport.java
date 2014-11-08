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
import com.logicbus.kvalue.context.KValueSource;
import com.logicbus.kvalue.core.Schema;
import com.logicbus.models.servant.ServiceDescription;

/**
 * KValue Source Report
 * 
 * @author duanyy
 *
 * @since 1.3.0.2
 */
public class KValueReport extends AbstractServant {

	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

	@Override
	protected int onXml(MessageDoc msgDoc, Context ctx) throws Exception {
		XMLMessage msg = (XMLMessage) msgDoc.asMessage(XMLMessage.class);		
		
		Element root = msg.getRoot();		
		Document doc = msg.getDocument();	
		
		String id = getArgument("id","all", msgDoc, ctx);	
		if (id == null || id.length() <= 0 || id.equals("all")){
			Element source = doc.createElement("source");
			
			KValueSource kvalue = KValueSource.get();				
			kvalue.report(source);
			
			root.appendChild(source);
		}else{						
			Schema schema = KValueSource.getSchema(id);
			if (schema != null){
				Element _schema = doc.createElement("schema");
				schema.report(_schema);
				root.appendChild(_schema);
			}
		}
		return 0;
	}

	@Override
	protected int onJson(MessageDoc msgDoc, Context ctx) throws Exception {
		JsonMessage msg = (JsonMessage)msgDoc.asMessage(JsonMessage.class);
		Map<String,Object> root = msg.getRoot();
		
		String id = getArgument("id","all", msgDoc, ctx);	
		if (id == null || id.length() <= 0 || id.equals("all")){
			Map<String,Object> source = new HashMap<String,Object>();
			
			KValueSource kvalue = KValueSource.get();				
			kvalue.report(source);
			
			root.put("source", source);
		}else{			
			Schema schema = KValueSource.getSchema(id);
			if (schema != null){
				Map<String,Object> _schema = new HashMap<String,Object>();
				schema.report(_schema);
				root.put("schema", _schema);
			}
		}
		return 0;
	}

}
