package com.alogic.sequence.service;

import java.util.HashMap;
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
 * 查询指定id的全局序列
 * 
 * @author duanyy
 * 
 * @since 1.6.4.4
 * 
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 */
public class SeqQuery extends AbstractServant {

	@Override
	protected int onXml(Context ctx) throws Exception{
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		String id = getArgument("id",ctx);
		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		SequenceSource src = SequenceSource.get();
		SequenceGenerator found = src.get(id);
		if (found == null){
			throw new ServantException("user.data_not_found","Can not find the seq generator :" + id);
		}
		
		Element elem = doc.createElement("seq");
		found.report(elem);
		root.appendChild(elem);

		return 0;
	}
	
	@Override
	protected int onJson(Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		String id = getArgument("id",ctx);
		
		SequenceSource src = SequenceSource.get();
		SequenceGenerator found = src.get(id);
		if (found == null){
			throw new ServantException("user.data_not_found","Can not find the seq generator :" + id);
		}
		
		Map<String,Object> map = new HashMap<String,Object>(); // NOSONAR
		found.report(map);
		msg.getRoot().put("seq", map);
		
		return 0;
	}
	
	@Override
	protected void onDestroy() {
		// nothing to do
	}
	
	@Override
	protected void onCreate(ServiceDescription sd){
		// nothing to do
	}

}