package com.logicbus.kvalue.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.kvalue.context.KValueSource;
import com.logicbus.kvalue.core.Schema;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 查询指定id的Schema信息
 * 
 * @author duanyy
 * @since 1.6.4.4
 */
public class SchemaQuery  extends AbstractServant {

	protected int onXml(Context ctx) throws Exception{
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		String id = getArgument("id",ctx);
		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		KValueSource src = KValueSource.get();
		Schema found = src.get(id);
		if (found == null){
			throw new ServantException("user.data_not_found","Can not find the kavalue schema :" + id);
		}
		
		Element elem = doc.createElement("schema");
		found.report(elem);
		root.appendChild(elem);

		return 0;
	}
	
	protected int onJson(Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		String id = getArgument("id",ctx);
		
		KValueSource src = KValueSource.get();
		Schema found = src.get(id);
		if (found == null){
			throw new ServantException("user.data_not_found","Can not find the kavalue schema :" + id);
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		found.report(map);
		msg.getRoot().put("schema", map);
		
		return 0;
	}
	
	@Override
	protected void onDestroy() {
		
	}
	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

}