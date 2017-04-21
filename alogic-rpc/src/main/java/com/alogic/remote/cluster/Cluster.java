package com.alogic.remote.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.remote.backend.AppBackends;
import com.alogic.remote.backend.BackendProvider;
import com.alogic.remote.route.Route;
import com.alogic.remote.route.impl.Simple;
import com.anysoft.util.Configurable;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.Watcher;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;


/**
 * 集群
 * 
 * 集群是路由的集合
 * 
 * @author duanyy
 * @version 1.1.11.3 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public interface Cluster extends Reportable,Configurable,XMLConfigurable,AutoCloseable{
	/**
	 * 获取id
	 * @return id
	 */
	public String getId();
		
	/**
	 * 获取缺省路由
	 * @return Route
	 */
	public Route getDefaultRoute();
	
	/**
	 * 根据ID获取一个内部预定义的路由
	 * @param id id
	 * @return Route
	 */
	public Route getRoute(String id);
	
	/**
	 * 获取Route的列表
	 * @return Route列表
	 */
	public Route[] getRoutes();
	
	/**
	 * 虚基类
	 * 
	 * @author duanyy
	 *
	 */
	public abstract static class Abstract implements Cluster,BackendProvider{
		/**
		 * a logger of log4j
		 */
		protected final static Logger LOG = LoggerFactory.getLogger(Cluster.class);
		
		/**
		 * id
		 */
		protected String id = null;
		
		/**
		 * 缺省的路由实现类
		 */
		protected String dftRouteClazz = Simple.class.getName();
		
		/**
		 * 缺省的路由id
		 */
		protected String dftRouteId = "default";
		
		/**
		 * 预定义的Route
		 */
		protected Map<String,Route> routes = new ConcurrentHashMap<String,Route>();		
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml,"id",getId());
				XmlTools.setString(xml, "module", getClass().getName());
				XmlTools.setString(xml,"dftRouteClass",dftRouteClazz);
				XmlTools.setString(xml, "dftRouteId", dftRouteId);
				
				if (!routes.isEmpty()){				
					Document doc = xml.getOwnerDocument();
					
					Route[] list = getRoutes();
					for (Route c:list){
						Element e = doc.createElement("route");
						XmlTools.setBoolean(e,"detail",false);
						c.report(e);					
						xml.appendChild(e);
					}
				}
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"id",getId());
				JsonTools.setString(json, "module", getClass().getName());
				JsonTools.setString(json,"dftRouteClass",dftRouteClazz);
				JsonTools.setString(json, "dftRouteId", dftRouteId);	
				
				if (!routes.isEmpty()){
					List<Object> clusters = new ArrayList<Object>();
					
					Route[] list = getRoutes();
					for (Route c:list){
						Map<String,Object> map = new HashMap<String,Object>();
						JsonTools.setBoolean(json, "detail", false);
						c.report(map);
						clusters.add(map);
					}
					
					json.put("route", clusters);
				}
			}
		}

		@Override
		public void configure(Properties p) {
			id = PropertiesConstants.getString(p,"id",id);
			dftRouteClazz = PropertiesConstants.getString(p,"dftRouteClass",dftRouteClazz);
			dftRouteId = PropertiesConstants.getString(p,"dftRouteId",dftRouteId);
		}
		
		@Override
		public void configure(Element root,Properties p){
			Properties props = new XmlElementProperties(root,p);
			configure(props);
			
			/**
			 * Route Factory
			 */
			Factory<Route> factory = new Factory<Route>(){
				public String getClassName(String module){
					if (module.indexOf(".") < 0){
						return "com.ketty.core.route.impl." + module;
					}
					return module;
				}			
			};			

			NodeList nodeList = XmlTools.getNodeListByPath(root, "route");

			for (int i = 0 ; i < nodeList.getLength(); i ++){
				Node n = nodeList.item(i);				
				if (Node.ELEMENT_NODE != n.getNodeType()){
					continue;
				}
				
				Element e = (Element)n;
				
				try {
					Route c = factory.newInstance(e, props, "module",Simple.class.getName());
					if (c != null && StringUtils.isNotEmpty(c.id())){
						c.setBackendProvider(this);
						routes.put(c.id(), c);
					}
				}catch (Exception ex){
					LOG.error("Can not create instance,Ignored.",ex);
				}
			}
			
			/**
			 * 如果没有定义缺省路由，则生成一个
			 */
			Route dftRoute = getDefaultRoute();
			if (dftRoute == null){
				dftRoute = new Simple(dftRouteId,this);
				routes.put(dftRouteId, dftRoute);
			}
		}
		
		@Override
		public void close() throws Exception {
			Route[] list = getRoutes();
			for (Route c:list){
				IOTools.close(c);
			}
			routes.clear();
		}

		@Override
		public Route getDefaultRoute() {
			return getRoute(dftRouteId);
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public Route getRoute(String id) {
			return routes.get(id);
		}

		@Override
		public void addWatcher(Watcher<AppBackends> watcher) {	
			// nothing to do
		}

		@Override
		public void removeWatcher(Watcher<AppBackends> watcher) {
			// nothing to do
		}
		
		@Override
		public Route[] getRoutes() {
			return routes.values().toArray(new Route[0]);
		}
	}
}
