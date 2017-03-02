package com.logicbus.models.servant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.logicbus.models.catalog.CatalogModel;
import com.logicbus.models.catalog.CatalogNode;
import com.logicbus.models.catalog.Path;

/**
 * Servant目录
 * @author hmyyduan
 * @version 1.6.4.46 [20160425 duanyy] <br>
 * - 实现Reportable,Configurable和XMLConfigurable接口 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.7.20 [20170302 duanyy] <br>
 * - 增加注销ServiceDescriptionWatcher的接口 <br>
 */
public interface ServantCatalog extends CatalogModel,Reportable,Configurable,XMLConfigurable {
	/**
	 * 在目录中查找指定ID的服务
	 * @param id 服务Id
	 * @return 服务描述信息
	 */
	public ServiceDescription findService(Path id);
	
	public void addWatcher(ServiceDescriptionWatcher watcher);
	
	public void removeWatcher(ServiceDescriptionWatcher watcher);
	
	/**
	 * 虚基类
	 * 
	 * @author duanyy
	 *
	 */
	public abstract static class Abstract implements ServantCatalog{
		/**
		 * a logger of log4j
		 */
		protected static Logger logger = LoggerFactory.getLogger(ServantCatalog.class);
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
				
				ServantCatalogNode root = (ServantCatalogNode) getRoot();
				if (root != null){
					Document doc = xml.getOwnerDocument();
					Element catalog = doc.createElement("catalog");
					report(root,catalog);
					xml.appendChild(catalog);
				}
			}
		}

		private void report(ServantCatalogNode root,Element xml){
			root.report(xml);
			Document doc = xml.getOwnerDocument();
			CatalogNode [] children = getChildren(root);
			if (children == null || children.length <= 0)
				return ;
			for (int i = 0 ; i < children.length ; i ++){
				Element catalog = doc.createElement("catalog");
				report((ServantCatalogNode) children[i],catalog);
				xml.appendChild(catalog);
			}			
		}
		
		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("module", getClass().getName());
				
				ServantCatalogNode root = (ServantCatalogNode) getRoot();
				if (root != null){
					Map<String,Object> catalog = new HashMap<String,Object>();
					report(root,catalog);
					json.put("catalog",catalog);
				}
			}
		}
		
		private void report(ServantCatalogNode root,Map<String,Object> json){
			root.toJson(json);
			CatalogNode [] children = getChildren(root);
			if (children == null || children.length <= 0)
				return ;
			
			List<Object> catalogs = new ArrayList<Object>();
			
			for (int i = 0 ; i < children.length ; i ++){
				Map<String,Object> catalog = new HashMap<String,Object>();
				report((ServantCatalogNode)children[i],catalog);
				catalogs.add(catalog);
			}
			
			json.put("catalog", catalogs);			
		}

		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}
		
	}
}
