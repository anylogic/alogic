package com.alogic.cache.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.cache.context.CacheSource;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * cache的Source报告
 * 
 * @author duanyy
 * @since 1.6.3.3
 */
public class CacheSourceReport extends AbstractServant {

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
		
		CacheSource src = CacheSource.get();
		src.report(eleSource);
		
		root.appendChild(eleSource);
		
		return 0;
	}
	protected int onJson(Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		CacheSource src = CacheSource.get();
		
		src.report(map);
		
		msg.getRoot().put("source", map);
		
		return 0;
	}
	
}
