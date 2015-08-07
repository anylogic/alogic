package com.alogic.doer.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.doer.core.TaskCenter;
import com.alogic.doer.core.TaskReport;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 查询指定的任务报告
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public class TaskReportReport extends AbstractServant {

	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

	protected int onXml(Context ctx) throws Exception{
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);
		
		TaskCenter tc = TaskCenter.TheFactory.get();
		
		if (tc == null){
			throw new ServantException("core.tc_not_found","Can not find a valid task center");
		}
		
		String id = getArgument("id",ctx);
		String queue = getArgument("queue",ctx);
		
		TaskReport report = tc.getTaskReport(id, queue);
		if (report == null){
			throw new ServantException("core.task_not_found","Can not find the task:" + id);
		}
		
		Document doc = msg.getDocument();
		Element _tc = doc.createElement("task");
		report.report(_tc);
		msg.getRoot().appendChild(_tc);
		return 0;
	}
	
	protected int onJson(Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		TaskCenter tc = TaskCenter.TheFactory.get();
		
		if (tc == null){
			throw new ServantException("core.tc_not_found","Can not find a valid task center");
		}
		
		String id = getArgument("id",ctx);
		String queue = getArgument("queue",ctx);
		
		TaskReport report = tc.getTaskReport(id, queue);
		if (report == null){
			throw new ServantException("core.task_not_found","Can not find the task:" + id);
		}
		
		Map<String,Object> _tc = new HashMap<String,Object>();
		report.report(_tc);
		
		msg.getRoot().put("task", _tc);
		return 0;
	}	
}
