package com.logicbus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.catalog.CatalogNode;
import com.logicbus.models.servant.ServantCatalog;
import com.logicbus.models.servant.ServantCatalogNode;
import com.logicbus.models.servant.ServantManager;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 查询服务列表
 * 
 * @author duanyy
 * @since 1.6.4.4
 * 
 */
public class ServiceList extends AbstractServant {
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
		ServantManager sm = ServantManager.get();
		ServantCatalog [] catalog = sm.getServantCatalog();
		Element services = doc.createElement("service");	// NOSONAR
		for (int i = 0 ; i < catalog.length ; i ++){
			ServantCatalogNode node = (ServantCatalogNode) catalog[i].getRoot();
			if (node != null){
				outputCatalog(catalog[i],node,services);
			}
		}
		root.appendChild(services);
		return 0;
	}

	
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		ServantManager sm = ServantManager.get();
		ServantCatalog [] catalog = sm.getServantCatalog();
		
		List<Object> services = new ArrayList<Object>(); // NOSONAR
		for (int i = 0 ; i < catalog.length ; i ++){
			ServantCatalogNode node = (ServantCatalogNode) catalog[i].getRoot();
			if (node != null){
				outputCatalog(catalog[i],node,services);
			}
		}
		
		msg.getRoot().put("service", services);
		return 0;
	}
	
	/**
	 * 输出目录
	 * @param catalog 目录
	 * @param root 节点
	 * @param e 输出的Element
	 */
	protected void outputCatalog(ServantCatalog catalog,ServantCatalogNode root,Element e){
		Document doc = e.getOwnerDocument();
		
		ServiceDescription [] services = root.getServices();
		
		for (ServiceDescription sd:services){
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
			
			e.appendChild(svc);
		}
		
		//迭代子节点
		CatalogNode [] children = catalog.getChildren(root);
		if (children == null || children.length <= 0)
			return ;
		for (int i = 0 ; i < children.length ; i ++){
			outputCatalog(catalog,(ServantCatalogNode)children[i],e);
		}
	}
	
	protected void outputCatalog(ServantCatalog catalog,ServantCatalogNode root,List<Object> json){
		ServiceDescription [] services = root.getServices();
		
		for (ServiceDescription sd:services){
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
			
			json.add(map);
		}
		
		//迭代子节点
		CatalogNode [] children = catalog.getChildren(root);
		if (children == null || children.length <= 0)
			return ;
		
		for (int i = 0 ; i < children.length ; i ++){
			outputCatalog(catalog,(ServantCatalogNode)children[i],json);
		}
	}	
}