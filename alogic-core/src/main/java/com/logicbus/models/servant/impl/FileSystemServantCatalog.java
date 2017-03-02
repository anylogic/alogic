package com.logicbus.models.servant.impl;

import java.io.File;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.CommandLine;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.logicbus.models.catalog.CatalogNode;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.DefaultServiceDescription;
import com.logicbus.models.servant.ServantCatalog;
import com.logicbus.models.servant.ServantCatalogNode;
import com.logicbus.models.servant.ServiceDescription;
import com.logicbus.models.servant.ServiceDescriptionWatcher;


/**
 * 基于文件系统的Servant目录实现
 * 
 * <br>
 * 可在{@link com.logicbus.models.servant.ServantManager ServantManager}的配置文件中配置此种类型的服务目录。
 * 
 * 例如：<br>
 * {@code 
 * <catalog module="com.logicbus.models.servant.impl.XMLResourceServantCatalog" xrc="/com/logicbus/service/servant.xml"
 * class="com.logicbus.models.servant.impl.XMLResourceServantCatalog" name="inner"/>
 * }
 * 
 * <br>
 * 本实现需要在catalog的XML节点中需要配置的属性包括：<br>
 * - module:实现类，取值为本实现的类名，即com.logicbus.models.servant.impl.FileSystemServantCatalog <br>
 * - home:服务目录在文件系统上的根目录，如果没有配置，则取全局的环境变量local.servant.home <br>
 * - local.servant.home:当没有配置home时启用，可在web.xml或全局配置文件中配置，缺省值为${local.home}/servants,其中local.home同样是全局变量 <br>
 * 
 * @author hmyyduan
 * 
 * @version 1.2.6 [20140801 duanyy]<br>
 * - ServiceDescription变更为interface,采用DefaultServiceDescription
 * 
 * @version 1.6.4.46 [20160425 duanyy] <br>
 * - 从ServantCatalog.Abstract上进行继承。 <br>
 * 
 * @version 1.6.7.20 [20170302 duanyy] <br>
 * - 增加注销ServiceDescriptionWatcher的接口 <br>
 * 
 */
public class FileSystemServantCatalog extends ServantCatalog.Abstract {

	/**
	 * 路径
	 */
	protected String rootPath;
	
	/**
	 * 目录名
	 */
	protected String domain;	
	
	public FileSystemServantCatalog(){

	}

	@Override
	public void configure(Properties p) {
		String path = p.GetValue("home", "");
		if (StringUtils.isEmpty(path)){
			rootPath = p.GetValue("local.servant.home", "${local.home}/servants");
		}else{
			rootPath = path;
		}
	}

	/**
	 * 在当前目录下查找服务定义信息
	 * @param id 服务的Id
	 */
	public ServiceDescription findService(Path id) {
		String pkg = id.getPackage();
		String serviceId = id.getId();
		ServantCatalogNodeImpl node = (ServantCatalogNodeImpl) getChildByPath(null,new Path(pkg));
		if (node == null){
			return null;
		}
		
		return node.findService(serviceId);
	}
	
	
	public CatalogNode getChildByPath(CatalogNode parent, Path _path) {
		if (parent == null){
			parent = getRoot();
			if (parent == null) return null;
		}
		
		if (_path.isRoot()){
			return parent;
		}

		String __path = rootPath + parent.getPath() + _path;
	
		File rootFile = new File(__path);
		if (!rootFile.isDirectory() || !rootFile.exists()){
			return null;
		}
		
		Path childPath = parent.getPath().append(_path);
		
		return createCatalogNode(childPath);
	}
	
	public CatalogNode[] getChildren(CatalogNode parent) {
		String __path = rootPath + parent.getPath();
		File rootFile = new File(__path);
		if (!rootFile.isDirectory() || !rootFile.exists()){
			return null;
		}
		
		Vector<CatalogNode> nodes = new Vector<CatalogNode>();
		File [] children = rootFile.listFiles();
		if (children != null){
			for (int i = 0 ; i < children.length ; i ++){
				File child = children[i];
				if (!child.isDirectory()){
					continue;
				}
				
				Path childPath = parent.getPath().append(child.getName());
				CatalogNode node = createCatalogNode(childPath);
				if (node != null){
					nodes.add(node);
				}
			}
		}		
		return nodes.toArray(new CatalogNode[0]);
	}
	
	public CatalogNode getRoot() {
		return createCatalogNode(new Path(""));
	}
	
	/**
	 * 根据路径和服务ID创建目录节点
	 * 
	 * <br>
	 * 本方法搜索本地文件目录_path下的以_name为名的.xml文件,将其服务描述装入内存.
	 * 
	 * @param _path 服务路径
	 * @return 目录节点实例
	 */
	protected CatalogNode createCatalogNode(Path _path){
		File rootFile = new File(rootPath + _path);
		if (!rootFile.isDirectory() || !rootFile.exists()){
			return null;
		}
		ServantCatalogNodeImpl root = new ServantCatalogNodeImpl(_path,null);
		
		File [] children = rootFile.listFiles();
		if (children != null){
			for (int i = 0 ; i < children.length ; i ++){
				File child = children[i];
				if (!child.isFile()){
					continue;
				}
				
				String name = child.getName();
				if (!name.endsWith(".xml"))
					continue;

				Document doc = null;
				try {
					doc = XmlTools.loadFromFile(child);
				} catch (Exception e) {
					continue;
				}
				
				ServiceDescription sd = toServiceDescription(_path,doc);
				if (sd == null){
					continue;
				}

				root.addService(sd.getServiceID(), sd);
			}
		}
		return root;
	}
	
	/**
	 * 从XML文档读入服务描述信息
	 * 
	 * <br>
	 * 一个典型的XML服务定义文档如下：<br>
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * <?xml version="1.0" encoding="GB2312"?>
	 * <service id="Helloworld2" name="Helloworld2" note="Helloworld ,我的第一个Logicbus服务。" visible="public" module="project.demo.service.Helloworld">
	 *     <properties>
	 *         <parameter id="welcome" value="北京欢迎你....."/>
	 *     </properties>
	 *     <modules>
	 *         <module url="file:///D:/software/anyLogicBusDemo-v1.0.0.jar"/>
	 *     </modules>
	 * </service>
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * 
	 * @see com.logicbus.models.servant.ServiceDescription#fromXML(Element)
	 * @see com.logicbus.models.servant.ServiceDescription#toXML(Element)
	 * 
	 * @param _path 父节点路径
	 * @param doc XML文档
	 * @return 服务描述
	 */
	protected ServiceDescription toServiceDescription(Path _path,Document doc){
		Element root = doc.getDocumentElement();
		String id = root.getAttribute("id");
		if (id == null){
			return null;
		}
		
		Path childPath = _path.append(id);
		DefaultServiceDescription sd = new DefaultServiceDescription(id);
		
		sd.fromXML(root);
		sd.setPath(childPath.getPath());
		
		return sd;
	}

	@Override
	public void addWatcher(ServiceDescriptionWatcher watcher) {
		// do nothing
	}

	@Override
	public void removeWatcher(ServiceDescriptionWatcher watcher) {
		// do noting
	}	
	
	public static void main(String [] args){				
		Settings settings = Settings.get();
		settings.SetValue("home", "D:\\ecloud\\logicbus\\servants");
		settings.addSettings(new CommandLine(args));
		
		ServantCatalog catalog = new FileSystemServantCatalog();
		catalog.configure(settings);
		ServantCatalogNode root = (ServantCatalogNode) catalog.getRoot();
		
		scanCatalog(catalog,root);				
		
		ServiceDescription sd = catalog.findService(new Path("/core/AclQuery"));
		if (sd != null){
			logger.info(sd.getPath());
		}
		
		sd = catalog.findService(new Path("/core/AclQuery2"));
		if (sd != null){
			logger.info(sd.getPath());
		}
	}
	
	private static void scanCatalog(ServantCatalog sc,CatalogNode root) {		
		CatalogNode[] children = sc.getChildren(root);

		logger.info("Package found:" + root.getPath());
		
		ServantCatalogNode servantCatalogNode = (ServantCatalogNode) root;
		ServiceDescription[] services = servantCatalogNode.getServices();
		
		logger.info("Service Cnt:" + services.length);
			
		for (ServiceDescription sd:services){
			logger.info(sd.getPath());
		}
		
		for (CatalogNode child : children) {
			scanCatalog(sc, child);
		}
	}
}
