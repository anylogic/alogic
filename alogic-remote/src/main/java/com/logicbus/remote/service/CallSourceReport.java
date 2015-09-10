package com.logicbus.remote.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;
import com.logicbus.remote.context.CallSource;

/**
 * 远程调用配置源报告
 * 
 * @author duanyy
 * @since 1.6.4.4
 * 
 */
public class CallSourceReport extends AbstractServant {

	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

	protected int onXml(Context ctx) throws Exception{
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		Element eleSource = doc.createElement("source");
		
		CallSource src = CallSource.get();
		src.report(eleSource);
		
		root.appendChild(eleSource);
		
		return 0;
	}
	protected int onJson(Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		CallSource src = CallSource.get();
		
		src.report(map);
		
		msg.getRoot().put("source", map);
		
		return 0;
	}
	
}