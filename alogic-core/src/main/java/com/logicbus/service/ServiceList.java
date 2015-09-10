package com.logicbus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
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
 */
public class ServiceList extends AbstractServant {
	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}
	
	protected int onXml(Context ctx) throws Exception {
		XMLMessage msg = (XMLMessage)ctx.asMessage(XMLMessage.class);
		Element root = msg.getRoot();
		Document doc = root.getOwnerDocument();
		ServantManager sm = ServantManager.get();
		ServantCatalog catalog[] = sm.getServantCatalog();
		Element services = doc.createElement("service");	
		for (int i = 0 ; i < catalog.length ; i ++){
			ServantCatalogNode node = (ServantCatalogNode) catalog[i].getRoot();
			if (node != null){
				outputCatalog(catalog[i],node,services);
			}
		}
		root.appendChild(services);
		return 0;
	}

	
	protected int onJson(Context ctx) throws Exception {
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		ServantManager sm = ServantManager.get();
		ServantCatalog catalog[] = sm.getServantCatalog();
		
		List<Object> services = new ArrayList<Object>();
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
			Element _service = doc.createElement("service");
			
			//仅仅输出简要信息
			//id
			_service.setAttribute("id",sd.getServiceID());
			//name
			_service.setAttribute("name", sd.getName());
			//note
			_service.setAttribute("note", sd.getNote());
			//module
			_service.setAttribute("module",sd.getModule());
			//visible
			_service.setAttribute("visible",sd.getVisible());
			//path
			_service.setAttribute("path",sd.getPath());
			//Properties
			_service.setAttribute("log", sd.getLogType().toString());
			
			e.appendChild(_service);
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
			Map<String,Object> map = new HashMap<String,Object>();
			
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