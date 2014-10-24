package com.logicbus.models.servant.impl;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import com.anysoft.util.Properties;
import com.anysoft.util.XmlTools;

import com.logicbus.models.catalog.CatalogNode;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;


/**
 * 基于元数据服务器的服务目录实现
 * <br>
 * 可在{@link com.logicbus.models.servant.ServantManager ServantManager}的配置文件中配置此种类型的服务目录。
 * 
 * 例如：<br>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <catalog module="com.logicbus.models.servant.impl.XMLResourceServantCatalog" 
 * xrc="/com/logicbus/service/servant.xml"
 * />
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <br>
 * 本实现需要在catalog的XML节点中需要配置的属性包括：<br>
 * - module:实现类，取值为本实现的类名，即com.logicbus.models.servant.impl.XMLResourceServantCatalog <br>
 * - xrc:文件地址，仅支持当前classLoader下的xml文件,缺省为:/com/logicbus/service/servant.xml
 * 
 * @author duanyy
 *
 */
public class MetadataServantCatalog extends XMLDocumentServantCatalog {
	protected String catalogXrcMaster = "${master.home}/services/core/manager/GetCatalogServices";
	protected String catalogXrcSecondary = "${secondary.home}/services/core/manager/GetCatalogServices";
	protected String serviceXrcMaster = "${master.home}/services/core/manager/GetServiceDesc";
	protected String serviceXrcSecondary = "${secondary.home}/services/core/manager/GetServiceDesc";
	
	public MetadataServantCatalog(Properties _properties) {

		super(_properties);
		
		catalogXrcMaster = _properties.GetValue("xrc.catalog.master", catalogXrcMaster);
		catalogXrcSecondary = _properties.GetValue("xrc.catalog.secondary", catalogXrcSecondary);
		
		serviceXrcMaster = _properties.GetValue("xrc.service.master", serviceXrcMaster);
		serviceXrcSecondary = _properties.GetValue("xrc.service.secondary", serviceXrcSecondary);
		
	}

	public void loadDocument(Properties _properties) {
		String xrc = _properties.GetValue("xrc.master",
				"${master.home}/services/core/manager/GetServantCatalog");
		if (xrc.length() <= 0) {
			return;
		}
		String xrcSecondary = _properties.GetValue("xrc.secondary",
				"${secondary.home}/services/core/manager/GetServantCatalog");
		if (xrc.length() <= 0) {
			return;
		}

		doc = loadDocument(xrc, xrcSecondary);
	}

	protected CatalogNode createCatalogNode(Path _path,Object _data){
		ServantCatalogNodeImpl catalogNode = new ServantCatalogNodeImpl(_path,_data);
		
		Document docCatalog = loadDocument(catalogXrcMaster+"?path="+_path,catalogXrcSecondary+"?path="+_path);
		if (docCatalog != null){
			Element root = docCatalog.getDocumentElement();
			if (root.getAttribute("code").equals("core.ok")){
				NodeList children = XmlTools.getNodeListByPath(root, "service");
				
				for (int i = 0 ; i < children.getLength() ; i ++){
					Node node = children.item(i);
					if (node.getNodeType() != Node.ELEMENT_NODE){
						continue;
					}
					Element e = (Element)node;
					if (!e.getNodeName().equals("service")){
						continue;
					}
					Path parentPath = new Path(_path.getPackage());
					
					ServiceDescription sd = toServiceDescription(parentPath,e);
					if (sd == null){
						continue;
					}
					catalogNode.addService(sd.getServiceID(), sd);			
				}
			}
		}
		return catalogNode;
	}
	
	
	public ServiceDescription findService(Path id) {
		Document docService = loadDocument(serviceXrcMaster+"?service="+id,
				serviceXrcSecondary+"?service="+id.getPath());
		if (docService != null){
			Element root = docService.getDocumentElement();
			if (root.getAttribute("code").equals("core.ok")){
				Element eService = XmlTools.getFirstElementByPath(root, "service");
				
				Path parentPath = new Path(id.getPackage());
				ServiceDescription sd = toServiceDescription(parentPath,eService);
				
				return sd;
			}
		}
		return null;
	}
}
