package com.logicbus.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.metrics.Fragment;
import com.alogic.metrics.stream.MetricsHandlerFactory;
import com.anysoft.stream.Handler;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 恢复指标处理
 * 
 * @author duanyy
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 将MessageDoc和Context进行合并整合 <br>
 * 
 * @version 1.6.6.13 [20170109 duanyy] <br>
 * - 采用新的指标接口
 */
public class MetricsResume extends AbstractServant{

	
	protected void onDestroy() {
	}

	
	protected void onCreate(ServiceDescription sd) throws ServantException{
	}

	
	protected int onXml(Context ctx) {
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);
		
		Handler<Fragment> handler = MetricsHandlerFactory.getClientInstance();
		
		if (handler != null){
			handler.resume();
			
			Element root = msg.getRoot();
			Document doc = root.getOwnerDocument();
			
			Element logger = doc.createElement(handler.getHandlerType());
			
			handler.report(logger);
			
			root.appendChild(logger);
		}
		return 0;
	}

	
	protected int onJson(Context ctx) {
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		Handler<Fragment> handler = MetricsHandlerFactory.getClientInstance();
		if (handler != null){
			handler.resume();
			
			Map<String,Object> root = msg.getRoot();
		
			Map<String,Object> logger = new HashMap<String,Object>();
			
			handler.report(logger);
			
			root.put(handler.getHandlerType(), logger);
		}
		return 0;
	}
}