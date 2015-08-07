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

/**
 * 定时器报告
 * @author duanyy
 * @since 1.6.3.37
 */
public class TimerReport extends AbstractServant {

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
		
		String id = getArgument("id",ctx);
		Timer timer = scheduler.get(id);
		if (timer == null){
			throw new ServantException("core.timer_not_found","Can not find the timer:" + id);
		}
		
		Document doc = msg.getDocument();
		
		Element _timer = doc.createElement("timer");
		timer.report(_timer);
		msg.getRoot().appendChild(_timer);
		
		return 0;
	}
	
	protected int onJson(Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		Scheduler scheduler = SchedulerFactory.get();
		if (scheduler == null){
			throw new ServantException("core.scheduler_not_found","Can not find a valid scheduler");
		}
		
		String id = getArgument("id",ctx);
		Timer timer = scheduler.get(id);
		if (timer == null){
			throw new ServantException("core.timer_not_found","Can not find the timer:" + id);
		}
		
		Map<String,Object> _timer = new HashMap<String,Object>();
		timer.report(_timer);
		msg.getRoot().put("timer", _timer);
		
		return 0;
	}	
}
