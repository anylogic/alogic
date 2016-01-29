package com.logicbus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.Settings;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantFactory;
import com.logicbus.backend.ServantPool;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

public class PooledServiceList extends AbstractServant {
	@Override
	protected void onDestroy() {
		// nothing to do
	}

	@Override
	protected void onCreate(ServiceDescription sd){
		// nothing to do
	}
	
	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage)ctx.asMessage(XMLMessage.class);
		Element root = msg.getRoot();
		Document doc = root.getOwnerDocument();
		
		Element service = doc.createElement("service");
		
		Settings settings = Settings.get();
		ServantFactory sf = (ServantFactory)settings.get("servantFactory");
		
		ServantPool [] pools = sf.getPools();
		
		for (ServantPool pool:pools){
			ServiceDescription sd = pool.getDescription();
			
			Element svc = doc.createElement("service");
			
			//仅仅输出简要信息
			//id
			svc.setAttribute("id",sd.getServiceID());
			//name
			svc.setAttribute("name", sd.getName());
			//note
			svc.setAttribute("note", sd.getNote());
			//module
			svc.setAttribute("module",sd.getModule());
			//visible
			svc.setAttribute("visible",sd.getVisible());
			//path
			svc.setAttribute("path",sd.getPath());
			//Properties
			svc.setAttribute("log", sd.getLogType().toString());
			
			svc.setAttribute("healthScore", String.valueOf(pool.getHealthScore()));
			svc.setAttribute("activeScore", String.valueOf(pool.getActiveScore()));
			
			service.appendChild(svc);			
		}
		
		root.appendChild(service);
		
		return 0;
	}

	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);

		Settings settings = Settings.get();
		ServantFactory sf = (ServantFactory)settings.get("servantFactory");
		
		ServantPool [] pools = sf.getPools();
		
		List<Object> service = new ArrayList<Object>();
		
		for (ServantPool pool:pools){
			ServiceDescription sd = pool.getDescription();
			Map<String,Object> map = new HashMap<String,Object>(); // NOSONAR
			
			//仅仅输出简要信息
			//id
			map.put("id",sd.getServiceID());
			//name
			map.put("name", sd.getName());
			//note
			map.put("note", sd.getNote());
			//module
			map.put("module",sd.getModule());
			//visible
			map.put("visible",sd.getVisible());
			//path
			map.put("path",sd.getPath());
			//Properties
			map.put("log", sd.getLogType().toString());		
			
			map.put("healthScore", pool.getHealthScore());
			map.put("activeScore", pool.getActiveScore());
			
			service.add(map);
		}

		msg.getRoot().put("service", service);
		return 0;
	}
}
