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
import com.logicbus.backend.ServantException;
import com.logicbus.backend.ServantFactory;
import com.logicbus.backend.ServantRegistry;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.catalog.CatalogNode;
import com.logicbus.models.servant.ServantCatalog;
import com.logicbus.models.servant.ServantCatalogNode;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 查询所有服务目录中配置的服务信息
 * 
 * <br>
 * 查询所有服务目录中配置的服务信息
 * 
 * 实现了一个内部核心服务，定义在/com/logicbus/service/servant.xml中，具体配置如下:<br>
 * 
 * {@code 
 * <service 
 * id="ServiceQuery" 
 * name="ServiceQuery" 
 * note="查询系统中所部署的所有服务"
 * visible="protected"
 * module="com.logicbus.service.ServiceQuery"
 * />
 * }
 * 
 * <br>
 * 本服务属于系统核心管理服务，内置了快捷访问,直接访问服务根目录即可访问.<br>
 * {@code
 * http://[host]:[port]/[webcontext]/services
 * }
 * <br>
 * 如果配置在服务器中，访问地址为：<br>
 * {@code
 * http://[host]:[port]/[webcontext]/services/core/ServiceQuery 
 * }
 * 
 * @author duanyy
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 将MessageDoc和Context进行合并整合 <br>
 * 
 * @version 1.6.3.27 [20150623 duanyy] <br>
 * - 增加XML和JSON双协议支持 <br>
 * 
 * @version 1.6.4.4 [20150910 duanyy] <br>
 * - 不再输出空的catalog <br>
 * 
 * @version 1.6.7.20 <br>
 * - 改造ServantManager模型,增加服务配置监控机制 <br>
 */
public class ServiceQuery extends AbstractServant {
	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}
	
	protected int onXml(Context ctx)  {
		XMLMessage msg = (XMLMessage)ctx.asMessage(XMLMessage.class);
		Element root = msg.getRoot();
		Document doc = root.getOwnerDocument();
		Settings settings = Settings.get();
		ServantFactory sf = (ServantFactory)settings.get("servantFactory");		
		ServantRegistry sm = sf.getServantRegistry();
		ServantCatalog catalog[] = sm.getServantCatalog();
		
		for (int i = 0 ; i < catalog.length ; i ++){
			Element catalogElem = doc.createElement("catalog");
			
			ServantCatalogNode node = (ServantCatalogNode) catalog[i].getRoot();
			if (node != null){
				outputCatalog(catalog[i],node,catalogElem);
			}
			root.appendChild(catalogElem);
		}
		return 0;
	}

	
	protected int onJson(Context ctx)  {
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		Settings settings = Settings.get();
		ServantFactory sf = (ServantFactory)settings.get("servantFactory");		
		ServantRegistry sm = sf.getServantRegistry();
		ServantCatalog catalog[] = sm.getServantCatalog();
		
		List<Object> _catalogs = new ArrayList<Object>();
		for (int i = 0 ; i < catalog.length ; i ++){
			ServantCatalogNode node = (ServantCatalogNode) catalog[i].getRoot();
			if (node != null){
				Map<String,Object> _catalog = new HashMap<String,Object>();
				outputCatalog(catalog[i],node,_catalog);
				_catalogs.add(_catalog);
			}
		}
		
		msg.getRoot().put("catalog", _catalogs);
		return 0;
	}
	
	/**
	 * 输出目录
	 * @param catalog 目录
	 * @param root 节点
	 * @param e 输出的Element
	 */
	protected void outputCatalog(ServantCatalog catalog,ServantCatalogNode root,Element e){
		root.toXML(e);
		Document doc = e.getOwnerDocument();
		CatalogNode [] children = catalog.getChildren(root);
		if (children == null || children.length <= 0)
			return ;
		for (int i = 0 ; i < children.length ; i ++){
			Element _e = doc.createElement("catalog");
			outputCatalog(catalog,(ServantCatalogNode)children[i],_e);
			e.appendChild(_e);
		}
	}
	
	protected void outputCatalog(ServantCatalog catalog,ServantCatalogNode root,Map<String,Object> json){
		root.toJson(json);
		CatalogNode [] children = catalog.getChildren(root);
		if (children == null || children.length <= 0)
			return ;
		
		List<Object> _catalogs = new ArrayList<Object>();
		
		for (int i = 0 ; i < children.length ; i ++){
			Map<String,Object> _catalog = new HashMap<String,Object>();
			outputCatalog(catalog,(ServantCatalogNode)children[i],_catalog);
			_catalogs.add(_catalog);
		}
		
		json.put("catalog", _catalogs);
	}	
}