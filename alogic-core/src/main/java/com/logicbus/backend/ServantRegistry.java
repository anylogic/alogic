package com.logicbus.backend;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServantCatalog;
import com.logicbus.models.servant.ServiceDescription;
import com.logicbus.models.servant.ServiceDescriptionWatcher;

/**
 * 服务注册表
 * 
 * <p>
 * 服务注册表用于为服务工厂提供服务规格配置信息
 * 
 * @author duanyy
 * 
 * @since 1.6.7.20
 * 
 */
public interface ServantRegistry extends Configurable,XMLConfigurable,AutoCloseable,Reportable{
	/**
	 * 查找指定服务的服务规格
	 * 
	 * <p>在服务目录中查找指定服务的服务规格配置信息，如果服务不存在，返回为空.
	 * @param id 指定的服务id
	 * @return 服务规格
	 */
	public ServiceDescription get(Path id);
	
	/**
	 * 获取当前服务目录列表
	 * @return 服务目录列表
	 */
	public ServantCatalog[] getServantCatalog();
	
	/**
	 * 增加监听器
	 * @param watcher 监听器
	 */
	public void addWatcher(ServiceDescriptionWatcher watcher);
	
	/**
	 * 注销监听器
	 * @param watcher 监听器
	 */
	public void removeWatcher(ServiceDescriptionWatcher watcher);
	
	/**
	 * 虚基类
	 * @author duanyy
	 *
	 */
	public static abstract class Abstract implements ServantRegistry{
		/**
		 * a slf4j logger
		 */
		protected static final Logger logger = LoggerFactory.getLogger(ServantRegistry.class);
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}

		@Override
		public void close()  {
			
		}

		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
			}
		}
		
	}
}
