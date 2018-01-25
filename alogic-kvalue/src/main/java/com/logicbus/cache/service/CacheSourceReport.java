package com.logicbus.cache.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.alogic.cache.naming.CacheStoreFactory;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * cache的Source报告
 * 
 * @author duanyy
 * @since 1.6.3.3
 * 
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 * 
 * @deprecated
 */
public class CacheSourceReport extends AbstractServant {

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
		
		CacheStoreFactory src = CacheStoreFactory.get();
		src.report(eleSource);
		
		root.appendChild(eleSource);
		
		return 0;
	}
	
	@Override
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		Map<String,Object> map = new HashMap<String,Object>(); // NOSONAR
		
		CacheStoreFactory src = CacheStoreFactory.get();
		
		src.report(map);
		
		msg.getRoot().put("source", map);
		
		return 0;
	}
	
}
