package com.logicbus.models.servant.impl;

import com.anysoft.util.CommandLine;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;

import com.logicbus.models.catalog.CatalogNode;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServantCatalog;
import com.logicbus.models.servant.ServantCatalogNode;
import com.logicbus.models.servant.ServiceDescription;


/**
 * 基于Java路径中XML文档的目录实现
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
 * - xrc:文件地址，缺省为:/com/logicbus/service/servant.xml <br>
 * - class:xrc所在的jar之中的class,通过该class来找到jar的URLLoader,缺省为:com.logicbus.models.servant.impl.XMLResourceServantCatalog <br>
 * 
 * @author duanyy
 * @version 1.6.4.46 [20160425 duanyy] <br>
 * - 从ServantCatalog.Abstract上进行继承。 <br>
 */
public class XMLResourceServantCatalog extends XMLDocumentServantCatalog {

	public XMLResourceServantCatalog() {
	}
	
	public void loadDocument(Properties _properties){
		String className = _properties.GetValue("class", 
				"com.logicbus.models.servant.impl.XMLResourceServantCatalog");
		
		String fileName = _properties.GetValue("xrc", "/com/logicbus/service/servant.xml");
		
		try {
			//采用当前的classLoader来装入类
			//@since 1.1.3
			Settings settings = Settings.get();
			ClassLoader cl = (ClassLoader)settings.get("classLoader");
			cl = cl != null ? cl : Thread.currentThread().getContextClassLoader();
			
			Class<?> clazz = cl.loadClass(className);
			doc = XmlTools.loadFromInputStream(clazz.getResourceAsStream(fileName));
		} catch (Exception ex){
			logger.fatal("Can not load xml config file:" + fileName, ex);
		}
	}
	
	public static void main(String [] args){				
		Settings settings = Settings.get();
		
		settings.addSettings(new CommandLine(args));
		
		ServantCatalog catalog = new XMLResourceServantCatalog();
		catalog.configure(settings);
		ServantCatalogNode root = (ServantCatalogNode) catalog.getRoot();
		
		scanCatalog(catalog,root);				
		
		ServiceDescription sd = catalog.findService(new Path("/core/AclQuery"));
		if (sd != null){
			logger.info(sd.getPath() + "/" + sd.getServiceID());
		}
		
		sd = catalog.findService(new Path("/core/AclQuery2"));
		if (sd != null){
			logger.info(sd.getPath() + "/" + sd.getServiceID());
		}
	}
	
	private static void scanCatalog(ServantCatalog sc,CatalogNode root) {		
		CatalogNode[] children = sc.getChildren(root);

		logger.info("Package found:" + root.getPath());
		
		ServantCatalogNode servantCatalogNode = (ServantCatalogNode) root;
		ServiceDescription[] services = servantCatalogNode.getServices();
		
		logger.info("Service Cnt:" + services.length);
			
		for (ServiceDescription sd:services){
			logger.info(sd.getPath() + "/" + sd.getServiceID());
		}
		
		for (CatalogNode child : children) {
			scanCatalog(sc, child);
		}
	}
}
