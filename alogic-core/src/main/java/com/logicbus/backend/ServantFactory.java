package com.logicbus.backend;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.Configurable;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.Settings;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServantManager;
import com.logicbus.models.servant.ServiceDescriptionWatcher;


/**
 * 服务员工厂
 * @author duanyy
 * @version 1.2.0 [20140607 duanyy]修正无法reload的bug
 * @version 1.2.2 [20140617 duanyy]
 * - 改进同步模型 <br>
 * 
 * @version 1.2.6 [20140807 duanyy]
 * - 修改为interface <br>
 * 
 * @version 1.6.7.20 <br>
 * - 改造为通过XML配置文件进行配置 <br>
 * - 改造ServantManager模型,增加服务配置监控机制 <br>
 */
public interface ServantFactory extends ServiceDescriptionWatcher,AutoCloseable,
	XMLConfigurable,Configurable,Reportable{
	/**
	 * 获得服务资源池列表
	 * @return 服务资源池列表
	 */
	public ServantPool [] getPools();
		
	/**
	 * 重新装入指定服务的资源池
	 * @param _id 服务id
	 * @return 服务资源池
	 * @throws ServantException
	 */
	public ServantPool reloadPool(Path _id);
	
	/**
	 * 获取指定服务的的服务资源池
	 * @param _id 服务Id
	 * @return 服务资源池
	 * @throws ServantException
	 */
	public ServantPool getPool(Path _id);
	
	/**
	 * 获取服务注册表
	 * @return 服务注册表
	 */
	public ServantRegistry getServantRegistry();
	
	/**
	 * 虚基类
	 * @author duanyy
	 *
	 */
	public static abstract class Abstract implements ServantFactory{
		/**
		 * a slf4j logger
		 */
		protected static final Logger logger = LoggerFactory.getLogger(ServantFactory.class);
		
		/**
		 * 服务注册表
		 */
		protected ServantRegistry servantRegistry = null;
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
			
			Element registry = XmlTools.getFirstElementByPath(e, "registry");
			if (registry != null){
				try{
					Factory<ServantRegistry> f = new Factory<ServantRegistry>();
					servantRegistry = f.newInstance(registry, props, "module", ServantManager.class.getName());
					servantRegistry.addWatcher(this);
				}catch (Exception ex){
					logger.error("Can not create servent registry,Using default.");
				}
			}
			
			if (servantRegistry == null){
				servantRegistry = new ServantManager();
				servantRegistry.addWatcher(this);
				servantRegistry.configure(e, p);
			}
			
			logger.info("Using servant registry:" + servantRegistry.getClass().getName());
		}

		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
				
				if (servantRegistry != null){
					Document doc = xml.getOwnerDocument();
					Element registry = doc.createElement("registry");
					
					servantRegistry.report(registry);
				}
				
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
				
				if (servantRegistry != null){
					Map<String,Object> map = new HashMap<String,Object>();
					servantRegistry.report(map);
					
					json.put("registry", map);
				}
			}
		}

		@Override
		public ServantRegistry getServantRegistry() {
			return servantRegistry;
		}
		
		public void close(){
			IOTools.close(servantRegistry);
		}
	}
	
	public static class TheFactory extends Factory<ServantFactory>{
		/**
		 * 缺省配置文件
		 */
		public static final String DEFAULT = 
				"java:///com/logicbus/backend/servantfactory.default.xml#com.logicbus.backend.ServantFactory";
		
		/**
		 * a logger of log4j
		 */
		protected static final Logger LOG = LoggerFactory.getLogger(ServantFactory.class);
		
		public static ServantFactory get(){
			return get(Settings.get());
		}
		
		public static ServantFactory get(Properties props){
			String master = props.GetValue("servant.factory.master",DEFAULT);
			String secondary = props.GetValue("servant.factory.secondary",DEFAULT);
			
			ResourceFactory rf = Settings.getResourceFactory();
			
			InputStream in = null;
			try {
				in = rf.load(master,secondary, null);
				Document doc = XmlTools.loadFromInputStream(in);		
				if (doc != null){
					return getServantFactory(doc.getDocumentElement(),props);
				}
			}catch (Exception ex){
				LOG.error("Error occurs when load xml file,source=" + master, ex);
			}finally {
				IOTools.closeStream(in);
			}
			return null;			
		}
		
		public static ServantFactory getServantFactory(Element e,Properties p){
			TheFactory factory = new TheFactory();
			return factory.newInstance(e, p);
		}
	}
}
