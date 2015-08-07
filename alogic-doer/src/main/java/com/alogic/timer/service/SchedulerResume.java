package com.alogic.timer.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.timer.core.Scheduler;
import com.alogic.timer.core.SchedulerFactory;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 恢复调度者处理
 * 
 * @author duanyy
 *
 */
public class SchedulerResume extends AbstractServant {

	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

	protected int onXml(Context ctx) throws Exception{
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);
		Scheduler scheduler = SchedulerFactory.get();
		if (scheduler == null){
			throw new ServantException("core.scheduler_not_found","Can not find a valid scheduler");
		}
		scheduler.resume();
		
		Document doc = msg.getDocument();
		
		Element _scheduler = doc.createElement("scheduler");
		scheduler.report(_scheduler);
		msg.getRoot().appendChild(_scheduler);
		
		return 0;
	}
	
	protected int onJson(Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		Scheduler scheduler = SchedulerFactory.get();
		if (scheduler == null){
			throw new ServantException("core.scheduler_not_found","Can not find a valid scheduler");
		}
		scheduler.resume();
		Map<String,Object> _scheduler = new HashMap<String,Object>();
		scheduler.report(_scheduler);
		msg.getRoot().put("scheduler", _scheduler);
		
		return 0;
	}	
}