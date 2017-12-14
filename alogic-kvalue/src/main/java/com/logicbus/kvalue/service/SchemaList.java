package com.logicbus.kvalue.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
 * 查询活跃的Schema列表
 * 
 * @author duanyy
 * @since 1.6.4.4
 * 
 */
public class SchemaList extends AbstractServant {

	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		KValueSource src = KValueSource.get();
		
		Collection<Schema> current = src.current();
		for (Schema c:current){
			Element elem = doc.createElement("schema");
			c.report(elem);
			root.appendChild(elem);
		}
		
		return 0;
	}
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		List<Object> list = new ArrayList<Object>();
		
		KValueSource src = KValueSource.get();
		
		Collection<Schema> current = src.current();
		for (Schema c:current){
			Map<String,Object> map = new HashMap<String,Object>();
			c.report(map);
			list.add(map);
		}
		
		msg.getRoot().put("schema", list);
		
		return 0;
	}
	
	@Override
	protected void onDestroy() {
		
	}
	@Override
	protected void onCreate(ServiceDescription sd) {

	}

}
