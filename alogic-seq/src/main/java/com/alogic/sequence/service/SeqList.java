package com.alogic.sequence.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.sequence.context.SequenceSource;
import com.alogic.sequence.core.SequenceGenerator;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 查询活跃的全局序列列表
 * 
 * @author duanyy
 * @since 1.6.4.4
 * 
 */
public class SeqList extends AbstractServant {

	protected int onXml(Context ctx) throws Exception{
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		SequenceSource src = SequenceSource.get();
		
		Collection<SequenceGenerator> current = src.current();
		for (SequenceGenerator instance:current){
			Element elem = doc.createElement("seq");
			instance.report(elem);
			root.appendChild(elem);
		}
		
		return 0;
	}
	protected int onJson(Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		List<Object> list = new ArrayList<Object>();
		
		SequenceSource src = SequenceSource.get();
		
		Collection<SequenceGenerator> current = src.current();
		for (SequenceGenerator instance:current){
			Map<String,Object> map = new HashMap<String,Object>();
			instance.report(map);
			list.add(map);
		}
		
		msg.getRoot().put("seq", list);
		
		return 0;
	}
	
	@Override
	protected void onDestroy() {
		
	}
	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

}