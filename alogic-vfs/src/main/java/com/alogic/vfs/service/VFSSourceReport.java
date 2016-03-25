package com.alogic.vfs.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.vfs.context.FileSystemSource;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 来源报告
 * 
 * @author duanyy
 *
 */
public class VFSSourceReport extends AbstractServant {

	@Override
	protected void onDestroy() {
		// nothing to do
	}

	@Override
	protected void onCreate(ServiceDescription sd) {
		// nothing to do
	}

	@Override
	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		Element eleSource = doc.createElement("source");
		
		FileSystemSource src = FileSystemSource.get();
		src.report(eleSource);
		
		root.appendChild(eleSource);
		
		return 0;
	}
	
	@Override
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		Map<String,Object> map = new HashMap<String,Object>(); // NOSONAR
		
		FileSystemSource src = FileSystemSource.get();
		
		src.report(map);
		
		msg.getRoot().put("source", map);
		
		return 0;
	}
	
}
