package com.alogic.doer.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.doer.core.TaskCenter;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * TaskCenterReport
 * @author yyduan
 * 
 * @since 1.6.9.3
 */
public class TaskCenterReport extends AbstractServant {

	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

	protected int onXml(Context ctx) {
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);
		TaskCenter tc = TaskCenter.TheFactory.get();
		if (tc == null){
			throw new ServantException("core.e1003","Can not find a valid task center");
		}
		
		Document doc = msg.getDocument();
		
		Element elem = doc.createElement("tc");
		tc.report(elem);
		msg.getRoot().appendChild(elem);
		
		return 0;
	}
	
	protected int onJson(Context ctx) {
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		TaskCenter tc = TaskCenter.TheFactory.get();
		if (tc == null){
			throw new ServantException("core.e1003","Can not find a valid task center");
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		tc.report(map);
		msg.getRoot().put("tc", map);
		
		return 0;
	}	
}
