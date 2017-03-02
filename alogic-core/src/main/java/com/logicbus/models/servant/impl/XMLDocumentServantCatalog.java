package com.logicbus.models.servant.impl;


import java.io.InputStream;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;
import com.logicbus.models.catalog.CatalogNode;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.DefaultServiceDescription;
import com.logicbus.models.servant.ServantCatalog;
import com.logicbus.models.servant.ServiceDescription;
import com.logicbus.models.servant.ServiceDescriptionWatcher;

/**
 * 基于XML文档的ServantCatalog实现
 * 
 * 本实现让你可以将所有的服务信息配置在一个XML文档中，可参考文件：<br>
 * {@code /com/logicbus/service/servant.xml}<br>
 * 
 * <br>
 * 可在{@link com.logicbus.models.servant.ServantManager ServantManager}的配置文件中配置此种类型的服务目录。
 * 
 * 例如：<br>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <catalog 
 * module="com.logicbus.models.servant.impl.XMLDocumentServantCatalog" 
 * xrc.master="${master.home}/servant.xml" 
 * /> 
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <br>
 * 本实现需要在catalog的XML节点中需要配置的属性包括：<br>
 * - module:实现类，取值为本实现的类名，即com.logicbus.models.servant.impl.XMLDocumentServantCatalog <br>
 * - xrc.master:文件地址，支持file,http等协议,缺省为:${master.home}/servant.xml
 * - xrc.secondary:备用文件地址,支持file,http等协议,缺省为:${secondary.home}/servant.xml
 * 
 * @author duanyy
 * @version 1.6.4.46 [20160425 duanyy] <br>
 * - 从ServantCatalog.Abstract上进行继承。 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.7.20 [20170302 duanyy] <br>
 * - 增加注销ServiceDescriptionWatcher的接口 <br>
 */
public class XMLDocumentServantCatalog extends ServantCatalog.Abstract {
	
	/**
	 * XML文档
	 */
	protected Document doc = null;
	
	public XMLDocumentServantCatalog(){
	}	
	
	@Override
	public void configure(Properties p) {
		loadDocument(p);
	}	
	
	/**
	 * 装入XML文档
	 * 
	 * <br>
	 * 在本方法装入文档，子类可重载此方法实现自己的文档加载。
	 * 
	 * @see com.logicbus.models.servant.impl.XMLResourceServantCatalog
	 * @see com.logicbus.models.servant.impl.MetadataServantCatalog
	 * 
	 * @param _properties
	 */
	public void loadDocument(Properties _properties){
		String xrc = _properties.GetValue("xrc.master", "${master.home}/servant.xml");
		if (xrc.length() <= 0){
			return ;
		}		
		String xrcSecondary = _properties.GetValue("xrc.secondary", "${secondary.home}/servant.xml");
		if (xrc.length() <= 0){
			return ;
		}			
		doc = loadDocument(xrc,xrcSecondary);
	}
	
	/**
	 * 从主/备地址中装入文档
	 * 
	 * @param master 主地址
	 * @param secondary 备用地址
	 * @return XML文档
	 */
	protected static Document loadDocument(String master,String secondary){
		Settings profile = Settings.get();
		ResourceFactory rm = (ResourceFactory) profile.get("ResourceFactory");
		if (null == rm){
			rm = new ResourceFactory();
		}
		
		Document ret = null;
		InputStream in = null;
		try {
			in = rm.load(master,secondary, null);
			ret = XmlTools.loadFromInputStream(in);		
		} catch (Exception ex){
			logger.error("Error occurs when load xml file,source=" + master, ex);
		}finally {
			IOTools.closeStream(in);
		}		
		return ret;
	}
	
	
	public CatalogNode getRoot() {
		if (doc == null) return null;
		Element data = doc.getDocumentElement();
		if (!data.getNodeName().equals("catalog")){
			data = (Element) XmlTools.getFirstElementByPath(data, "catalog");
			if (data == null) return null;
		}

		return createCatalogNode(new Path(""),data);
	}

	/**
	 * 根据路径和服务ID创建目录节点
	 * 
	 * <br>
	 * 本方式直接从附加数据中获得父节点，在父节点下创建本节点
	 * 
	 * @param _path 服务路径
	 * @param _data 附加数据，在本实现中是父节点实例
	 * @return 目录节点实例
	 */
	protected CatalogNode createCatalogNode(Path _path,Object _data){
		ServantCatalogNodeImpl catalogNode = new ServantCatalogNodeImpl(_path,_data);
		
		NodeList children = ((Element)_data).getChildNodes();
		
		for (int i = 0 ; i < children.getLength() ; i ++){
			Node node = children.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			Element e = (Element)node;
			if (!e.getNodeName().equals("service")){
				continue;
			}
			
			ServiceDescription sd = toServiceDescription(_path,e);
			if (sd == null){
				continue;
			}
			
			catalogNode.addService(sd.getServiceID(), sd);			
		}
		return catalogNode;
	}
	
	/**
	 * 从XML节点中提取服务描述信息
	 * @param _path 父节点路径
	 * @param root XML节点
	 * @return 服务描述信息
	 */
	protected ServiceDescription toServiceDescription(Path _path,Element root){
		String id = root.getAttribute("id");
		if (id == null){
			return null;
		}
		
		Path childPath = _path.append(id);
		
		DefaultServiceDescription sd = new DefaultServiceDescription(childPath.getId());
		sd.fromXML(root);
		sd.setPath(childPath.getPath());
		
		return sd;
	}
	
	public CatalogNode[] getChildren(CatalogNode parent) {
		Element data = (Element)parent.getData();
		Vector<CatalogNode> nodes = new Vector<CatalogNode>();
		NodeList children = data.getChildNodes();
		if (children != null){
			for (int i = 0 ; i < children.getLength() ; i ++){
				Node child = children.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE)continue;
				Element e = (Element)child;
				if (!e.getNodeName().equals("catalog")){
					continue;
				}
				String name = e.getAttribute("name");
				
				Path childPath = parent.getPath().append(name);
				CatalogNode node = createCatalogNode(childPath,e);
				
				if (node != null){
					nodes.add(node);
				}
			}
		}		
		return nodes.toArray(new CatalogNode[0]);		
	}

	
	public CatalogNode getChildByPath(CatalogNode _parent, Path  _path) {
		if (_parent == null){
			_parent = getRoot();
			if (_parent == null) return null;
		}
		
		if (_path.isRoot()) return _parent;

		Element root = (Element)_parent.getData();
		Element found = findCatalogByPath(root,_path.getPath());

		if (found == null)
			return null;
		
		Path childPath = _parent.getPath().append(_path);
		
		return createCatalogNode(childPath,found);
	}
	
	/**
	 * 通过路径找到目录节点
	 * @param _root 根节点
	 * @param _path 路径
	 * @return 目录节点
	 */
	static private Element findCatalogByPath(Element _root,String _path){
		String current;
		String left;
		//_path like '//com/logicbus/models/servant'
		// first found 'com'
		{
			//find the first char who is not '/'
			int start = 0;
			for (; start < _path.length(); start ++){
				if (_path.charAt(start) != '/')break;
			}
			//find the end of the current
			int end = start;
			for (;end < _path.length() ; end++){
				if (_path.charAt(end) == '/') break;
			}
			
			current = _path.substring(start,end);
			if (end >= _path.length()){
				left = "";
			}else{
				left = _path.substring(end + 1);
			}
		}
		NodeList children = _root.getChildNodes();
		for (int i = 0 ; i < children.getLength() ; i ++){
			Node node = children.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE)continue;
			Element e = (Element)node;
			if (!e.getNodeName().equals("catalog")){
				continue;
			}
			if (e.getAttribute("name").equals(current)){
				//found
				if (left.length() <= 0) return e;
				return findCatalogByPath(e,left);
			}
		}
		return null;
	}
	
	
	public ServiceDescription findService(Path id) {
		String pkg = id.getPackage();
		String serviceId = id.getId();
		ServantCatalogNodeImpl node = (ServantCatalogNodeImpl) getChildByPath(null,new Path(pkg));
		if (node == null){
			return null;
		}
		
		return node.findService(serviceId);
	}
	
	@Override	
	public void addWatcher(ServiceDescriptionWatcher watcher) {
		//do nothing
	}

	@Override
	public void removeWatcher(ServiceDescriptionWatcher watcher) {
		// do noting
	}	


}
