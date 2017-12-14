package com.alogic.timer.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.timer.core.Scheduler;
import com.alogic.timer.core.SchedulerFactory;
import com.alogic.timer.core.Timer;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

public class TimerReload extends AbstractServant {

	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) {

	}

	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);
		Scheduler scheduler = SchedulerFactory.get();
		if (scheduler == null){
			throw new ServantException("core.e1003","Can not find a valid scheduler");
		}
		
		String id = getArgument("id",ctx);
		Timer timer = scheduler.get(id);
		if (timer == null){
			throw new ServantException("clnt.e2007","Can not find the timer:" + id);
		}
		timer.reload();
		Document doc = msg.getDocument();
		
		Element _timer = doc.createElement("timer");
		timer.report(_timer);
		msg.getRoot().appendChild(_timer);
		
		return 0;
	}
	
	protected int onJson(Context ctx) {
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		Scheduler scheduler = SchedulerFactory.get();
		if (scheduler == null){
			throw new ServantException("core.e1003","Can not find a valid scheduler");
		}
		
		String id = getArgument("id",ctx);
		Timer timer = scheduler.get(id);
		if (timer == null){
			throw new ServantException("clnt.e2007","Can not find the timer:" + id);
		}
		timer.reload();
		Map<String,Object> _timer = new HashMap<String,Object>();
		timer.report(_timer);
		msg.getRoot().put("timer", _timer);
		
		return 0;
	}	
}
