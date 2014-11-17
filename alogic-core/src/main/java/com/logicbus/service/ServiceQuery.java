package com.logicbus.service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.catalog.CatalogNode;
import com.logicbus.models.servant.ServantCatalog;
import com.logicbus.models.servant.ServantCatalogNode;
import com.logicbus.models.servant.ServantManager;

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
 */
public class ServiceQuery extends Servant {
	
	
	public int actionProcess(Context ctx) throws Exception {
		XMLMessage msg = (XMLMessage)ctx.asMessage(XMLMessage.class);
		Element root = msg.getRoot();
		Document doc = root.getOwnerDocument();
		ServantManager sm = ServantManager.get();
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
		if (children == null)
			return ;
		for (int i = 0 ; i < children.length ; i ++){
			Element _e = doc.createElement("catalog");
			outputCatalog(catalog,(ServantCatalogNode)children[i],_e);
			e.appendChild(_e);
		}
	}
}