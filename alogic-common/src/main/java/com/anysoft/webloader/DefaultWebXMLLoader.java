package com.anysoft.webloader;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;


/**
 * 缺省的WebXMLLoader
 * 
 * @author duanyy
 * 
 * @since 1.6.0.0
 *
 */
public class DefaultWebXMLLoader implements WebXMLLoader {
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(DefaultWebXMLLoader.class);
	/**
	 * 构造函数
	 * 
	 * @param p 环境变量
	 */
	public DefaultWebXMLLoader(Properties p){
		
	}
	
	/**
	 * 将XML节点中的内容装入到ServletContext
	 * 
	 * @param settings
	 * @param root
	 * @param sc
	 */
	public void load(Properties settings,Element root, ServletContext sc) {
		loadServlets(settings,root,sc);
		loadFilters(settings,root,sc);
	}

	/**
	 * 装入filters，可依次装入多个，路径为/filter
	 * 
	 * @param settings
	 * @param root
	 * @param sc
	 */
	protected void loadFilters(Properties settings, Element root,
			ServletContext sc) {
		NodeList _servlets = XmlTools.getNodeListByPath(root, "filter");
		if (_servlets != null && _servlets.getLength() > 0){
			for (int i = 0 ; i < _servlets.getLength() ; i ++){
				Node n = _servlets.item(i);
				
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				loadFilter(settings,(Element)n,sc);
			}
		}
	}

	/**
	 * 将指定节点的内容作为filter装入
	 * @parameter settings
	 * @param n
	 * @param sc
	 */
	protected void loadFilter(Properties settings, Element n, ServletContext sc) {
		Properties p = new XmlElementProperties(n,settings);
		String filterName = PropertiesConstants.getString(p,"filter-name","",true);
		String filterClazz = PropertiesConstants.getString(p,"filter-class","",true);
		
		if (filterName != null && filterName.length() > 0 && filterClazz != null && filterClazz.length() > 0){
			try {
				logger.info("Filter is found:" + filterName + "/" + filterClazz);
				FilterRegistration.Dynamic filter = sc.addFilter(filterName, filterClazz);
				//async-supported
				filter.setAsyncSupported(PropertiesConstants.getBoolean(p, "async-supported", false));
				//init-parameters
				{
					NodeList _params = XmlTools.getNodeListByPath(n, "init-param");
					
					if (_params != null && _params.getLength() > 0){
						for (int i = 0 ; i < _params.getLength() ; i ++){
							Node _n = _params.item(i);
							if (n.getNodeType() != Node.ELEMENT_NODE){
								continue;
							}
							
							Element _e = (Element)_n;
							
							String name = _e.getAttribute("param-name");
							String value = _e.getAttribute("param-value");
							if (name != null && value != null && name.length() > 0 && value.length() > 0){
								String _value = settings.transform(value);
								logger.info("add filter parameter:" + name + "=" + _value);
								filter.setInitParameter(name, _value);
							}
						}
					}
				}
				//mappings
				{
					NodeList _mappings = XmlTools.getNodeListByPath(n, "filter-mapping");
					if (_mappings != null && _mappings.getLength() > 0){
						for (int i = 0 ;i < _mappings.getLength() ; i ++){
							Node _n = _mappings.item(i);
							if (_n.getNodeType() != Node.ELEMENT_NODE){
								continue;
							}
							
							String _dispatcher = ((Element)_n).getAttribute("dispatcher");
							
							EnumSet<DispatcherType> dispatcher = getDispatcher(_dispatcher);

							String urlPattern = ((Element)_n).getAttribute("url-pattern");
							if (urlPattern != null && urlPattern.length() > 0){
								//url-pattern
								filter.addMappingForUrlPatterns(dispatcher, true, urlPattern);
								logger.info("Add filter mapping:" + urlPattern);
							}else{
								String servletName = ((Element)_n).getAttribute("servlet-name");
								if (servletName != null && servletName.length() > 0){
									//servlet-name
									filter.addMappingForServletNames(dispatcher, true, servletName);
									logger.info("Add filter mapping:" + servletName);
								}
							}
						}
					}
				}
				logger.error("Succeeded in adding servlet:" + filterName + "/" + filterClazz);
			}catch (Exception ex){
				logger.error("Failed to add servlet:" + filterName + "/" + filterClazz);
			}
		}
	}

	private static EnumSet<DispatcherType> getDispatcher(String _dispatcher) {
		EnumSet<DispatcherType> result = null;
		if (_dispatcher != null && _dispatcher.length() > 0){
			result = EnumSet.noneOf(DispatcherType.class);
			
			String [] _dispatchers = _dispatcher.split("[:]");
			if (_dispatchers != null && _dispatchers.length > 0){
				for (int i = 0 ;i < _dispatchers.length ; i ++){
					String _e = _dispatchers[i];
					if (_e == null || _e.length() <= 0){
						continue;
					}
					try {
						DispatcherType dt = DispatcherType.valueOf(_e.toUpperCase());
						if (dt != null){
							result.add(dt);
						}
					}catch (Exception ex){
						logger.error("Can not parse the dispatcher type : " + _e);
					}
				}
			}
			
			if (result.isEmpty()){
				result.add(DispatcherType.REQUEST);
			}
		}else{
			result = EnumSet.of(DispatcherType.REQUEST);
		}
		return result;
	}

	/**
	 * 装入servlet，可依次装入多个，路径为/servlet
	 * 
	 * @param settings
	 * @param root
	 * @param sc
	 */
	protected void loadServlets(Properties settings,Element root, ServletContext sc) {
		NodeList _servlets = XmlTools.getNodeListByPath(root, "servlet");
		if (_servlets != null && _servlets.getLength() > 0){
			for (int i = 0 ; i < _servlets.getLength() ; i ++){
				Node n = _servlets.item(i);
				
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				loadServlet(settings,(Element)n,sc);
			}
		}
	}

	/**
	 * 将指定节点的内容作为servlet装入
	 * @parameter settings
	 * @param n
	 * @param sc
	 */
	protected void loadServlet(Properties settings,Element n, ServletContext sc) {
		Properties p = new XmlElementProperties(n,settings);
		String servletName = PropertiesConstants.getString(p,"servlet-name","",true);
		String servletClazz = PropertiesConstants.getString(p,"servlet-class","",true);
		
		if (servletName != null && servletName.length() > 0 && servletClazz != null && servletClazz.length() > 0){
			try {
				logger.info("Servlet is found:" + servletName + "/" + servletClazz);
				
				ServletRegistration.Dynamic servlet = sc.addServlet(servletName, servletClazz);
				//loadOnStartup
				servlet.setLoadOnStartup(PropertiesConstants.getInt(p, "load-on-startup",1));
				//async-supported
				servlet.setAsyncSupported(PropertiesConstants.getBoolean(p, "async-supported", false));				
				//init-parameters
				{
					NodeList _params = XmlTools.getNodeListByPath(n, "init-param");
					
					if (_params != null && _params.getLength() > 0){
						for (int i = 0 ; i < _params.getLength() ; i ++){
							Node _n = _params.item(i);
							if (n.getNodeType() != Node.ELEMENT_NODE){
								continue;
							}
							
							Element _e = (Element)_n;
							
							String name = _e.getAttribute("param-name");
							String value = _e.getAttribute("param-value");
							if (name != null && value != null && name.length() > 0 && value.length() > 0){
								String _value = settings.transform(value);
								logger.info("Add servlet parameter:" + name + "=" + _value);
								servlet.setInitParameter(name, _value);
							}
						}
					}
				}				
				//mappings
				{
					NodeList _mappings = XmlTools.getNodeListByPath(n, "servlet-mapping");
					if (_mappings != null && _mappings.getLength() > 0){
						for (int i = 0 ;i < _mappings.getLength() ; i ++){
							Node _n = _mappings.item(i);
							if (_n.getNodeType() != Node.ELEMENT_NODE){
								continue;
							}
							
							String mapping = ((Element)_n).getAttribute("url-pattern");
							if (mapping != null && mapping.length() > 0){
								String _mapping = p.transform(mapping);
								servlet.addMapping(_mapping);
								logger.info("add servlet mapping:" + _mapping);
							}
						}
					}
				}
				logger.info("Succeeded in adding servlet:" + servletName + "/" + servletClazz);
			}catch (Exception ex){
				logger.error("Failed to add servlet:" + servletName + "/" + servletClazz);
			}
		}
	}
}
