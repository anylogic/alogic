package com.logicbus.models.servant;

import java.io.InputStream;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;
import com.logicbus.models.catalog.Path;
import com.logicbus.backend.ServantRegistry;

/**
 * 服务规格管理器
 * 
 * <br>
 * 服务规格管理器负责从服务目录中读取服务规格信息,可支持一到多个服务目录.配置文件地址可以从web.xml或者全局参数中读取，参数包括：<br>
 * 
 * - master.servant.config 配置文件URL地址,缺省为：java:///com/logicbus/models/servant/default.xml<br>
 * - secondary.servant.config 配置文件备用URL地址,缺省为:java:///com/logicbus/models/servant/default.xml<br>
 * 
 * 缺省的配置文件内容如下:<br>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *     <?xml version="1.0" encoding="UTF-8"?>
 *     <catalogs>
 *         <catalog module="com.logicbus.models.servant.impl.XMLResourceServantCatalog" xrc="/com/logicbus/service/servant.xml"
 *         class="com.logicbus.models.servant.impl.XMLResourceServantCatalog" name="inner"/>
 *         <!-- 
 *         <catalog module="com.logicbus.models.servant.impl.XMLDocumentServantCatalog" xrc.master="${master.home}/servant.xml" name="outter"/>
 *         -->
 *         <!-- 
 *         <catalog module="com.logicbus.models.servant.impl.FileSystemServantCatalog" home="${local.home}/servants"/>
 *         -->	
 *     </catalogs>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
 * 
 * <br>
 * 
 * 由于{@link ServantManager}在查找服务时是一个一个服务目录查找的,因此配置文件中的服务目录次序就很重要,如果配置在前面,将优先被搜索到.
 * 例如配置了服务目录A和服务目录B，而A和B中都包含了服务/demo/logicbus/Helloworld,由于A配置在前面，将永远搜索到A提供的/demo/logicbus/Helloworld，
 * 而B中提供的/demo/logicbus/Helloworld服务则永远不会被调用.<br>
 * 
 * @author duanyy
 *
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.7.20 <br>
 * - 改造ServantManager模型,增加服务配置监控机制 <br>
 */
public class ServantManager extends ServantRegistry.Abstract{
	
	/**
	 * 缺省配置文件
	 */
	protected static final String DEFAULT 
		= "java:///com/logicbus/models/servant/servantcatalog.default.xml#com.logicbus.backend.server.LogicBusApp"; 

	/**
	 * 服务目录列表
	 */
	protected Vector<ServantCatalog> catalogs = new Vector<ServantCatalog>();

	/**
	 * Constructor
	 *  
	 */
	public ServantManager(){
	
	}
	
	/**
	 * 查询指定服务的服务规格
	 * @param id 服务ID
	 * @return 服务规格
	 */
	public ServiceDescription get(Path id){
		for (int i = 0 ; i < catalogs.size() ; i ++){
			ServantCatalog __catalog = catalogs.elementAt(i);
			if (__catalog == null){
				continue;
			}
			ServiceDescription sd = __catalog.findService(id);
			if (sd != null){
				return sd;
			}
		}
		return null;
	}
		
	@Override
	public void configure(Properties p) {
		String configFile = PropertiesConstants.getString(p,"servant.config.master",DEFAULT,false);
		String secondaryFile = PropertiesConstants.getString(p,"servant.config.secondary",DEFAULT,false);
	
		InputStream in = null;
		try {
			ResourceFactory rf = Settings.getResourceFactory();
			in = rf.load(configFile,secondaryFile, null);
			Document doc = XmlTools.loadFromInputStream(in);		
			loadConfig(p,doc);
		} catch (Exception ex){
			logger.error("Error occurs when load xml file,source=" + configFile, ex);
		}finally {
			IOTools.closeStream(in);
		}	
	}	
	
	/**
	 * 读取配置信息
	 * 
	 * <br>
	 * 在{@link #configure(Properties)}中调用.<br>
	 * 
	 * @param doc 配置文档
	 * 
	 */
	private void loadConfig(Properties p,Document doc) {
		if (doc == null) return;
		
		Element root = doc.getDocumentElement();
		
		Factory<ServantCatalog> factory = new Factory<ServantCatalog>();
		
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength() ; i++){
			Node item = children.item(i);
			if (item.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			Element e = (Element)item;
			if (!e.getNodeName().equals("catalog")){
				continue;
			}
			
			try {
				ServantCatalog servantCatalog = factory.newInstance(e, p, "module");
				if (servantCatalog != null){
					catalogs.add(servantCatalog);
				}
			}catch (Exception ex){
				logger.error("Can not create instance of ServantCatalog.", ex);
			}
		}
		
	}
	
	/**
	 * 获得服务目录列表
	 * 
	 * @return 服务目录列表
	 */
	public ServantCatalog[] getServantCatalog(){
		return (ServantCatalog[]) catalogs.toArray(new ServantCatalog[0]);
	}
	
	@Override
	public void addWatcher(ServiceDescriptionWatcher watcher){
		for (ServantCatalog catalog:catalogs){
			if (catalog != null){
				catalog.addWatcher(watcher);
			}
		}
	}

	@Override
	public void removeWatcher(ServiceDescriptionWatcher watcher) {
		for (ServantCatalog catalog:catalogs){
			if (catalog != null){
				catalog.removeWatcher(watcher);
			}
		}
	}
}
