package com.alogic.remote.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.remote.backend.AppBackends;
import com.alogic.remote.backend.Backend;
import com.alogic.remote.backend.BackendProvider;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 应用路由
 * @author duanyy
 *
 */
public interface Route extends Reportable,Configurable,XMLConfigurable,AutoCloseable{
	
	/**
	 * 获取id
	 * @return id
	 */
	public String id();
	
	/**
	 * 找到指定应用指定规则的路由
	 * 
	 * @param app 应用
	 * @param route 路由
	 * @return 后端节点列表
	 */
	public List<Backend> select(String app,String route);
	
	/**
	 * 找到指定应用指定规则的路由
	 * @param app 应用
	 * @param p 变量集
	 * @return 后端节点列表
	 */
	public List<Backend> select(String app,Properties p);
	
	/**
	 * 设置provider
	 * @param provider 提供者
	 */
	public void setBackendProvider(BackendProvider provider);
	
	/**
	 * 重构路由
	 * @param app 变更的应用的路由信息
	 */
	public void rebuild(AppBackends app);
	
	/**
	 * 虚基类
	 * 
	 * @author duanyy
	 *
	 */
	public abstract class Abstract implements Route{
		private String id;
		private BackendProvider provider = null;
		
		public Abstract(){
			
		}
		
		public Abstract(String id,BackendProvider provider){
			this.id = id;
			this.provider = provider;
		}
		
		@Override
		public String id(){
			return id;
		}
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml,"module",getClass().getName());
				XmlTools.setString(xml,"id",id());			
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
				JsonTools.setString(json,"id",id());
			}
		}

		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);		
			configure(props);
		}
		
		@Override
		public void configure(Properties p) {
			id = PropertiesConstants.getString(p,"id","",true);
		}	
		
		@Override
		public void close() throws Exception {
			// nothing to do
		}	
		
		public AppBackends get(String id){
			return provider.load(id);
		}
		
		@Override
		public void setBackendProvider(BackendProvider p){
			provider = p;
		}
	}
	
	/**
	 * 指定应用的路由
	 * @author duanyy
	 * @version 1.1.10.23 [20161118 duanyy] <br>
	 * - 支持超时，只能缓存30分钟 <br>
	 */
	public static class AppRoute implements Reportable{
		protected Map<String,List<Backend>> routes = new ConcurrentHashMap<String,List<Backend>>();
		protected long timestamp = System.currentTimeMillis();
		public synchronized void add(String route,Backend backend){
			List<Backend> found = routes.get(route);
			if (found == null){
				found = new ArrayList<Backend>();
				routes.put(route,found);
			}
			found.add(backend);
		}
		
		public void clear(){
			routes.clear();
		}
		
		public List<Backend> get(String route){
			return routes.get(route);
		}
		
		public boolean isExpired(){
			return System.currentTimeMillis() - timestamp > 30 * 60 * 1000L;
		}

		@Override
		public void report(Element xml) {
			if (xml != null){
				Document doc = xml.getOwnerDocument();
				
				Iterator<Entry<String,List<Backend>>> iter = routes.entrySet().iterator();				
				while (iter.hasNext()){
					Entry<String,List<Backend>> entry = iter.next();
					
					Element item = doc.createElement("item");
					
					XmlTools.setString(item,"id",entry.getKey());
					
					List<Backend> list = entry.getValue();
					for (Backend b:list){
						Element backend = doc.createElement("backend");
						b.report(backend);
						item.appendChild(backend);
					}
					
					xml.appendChild(item);
				}
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				Iterator<Entry<String,List<Backend>>> iter = routes.entrySet().iterator();	
				List<Object> items = new ArrayList<Object>();
				while (iter.hasNext()){
					Entry<String,List<Backend>> entry = iter.next();
					
					Map<String,Object> item = new HashMap<String,Object>();
					JsonTools.setString(item,"id",entry.getKey());
					
					List<Object> backends = new ArrayList<Object>();
					List<Backend> list = entry.getValue();
					for (Backend b:list){
						Map<String,Object> map = new HashMap<String,Object>();
						b.report(map);
						backends.add(map);
					}
					item.put("backend", backends);
					items.add(item);
				}
				json.put("item", items);
			}
		}
	}
	
	/**
	 * 基于二级索引的实现
	 * 
	 * @author duanyy
	 *
	 */
	public abstract class Indexed extends Abstract{
		/**
		 * app的路由
		 */
		protected Map<String,AppRoute> routes = new ConcurrentHashMap<String,AppRoute>();
		
		public Indexed(){
			
		}
		
		public Indexed(String id,BackendProvider provider){
			super(id,provider);
		}
		
		@Override
		public List<Backend> select(String app, String route) {
			AppRoute ar = routes.get(app);

			if (ar == null || ar.isExpired()){
				synchronized(this){
					if (ar == null){
						ar = new AppRoute();
						routes.put(app, ar);
						
						AppBackends appBackends = get(app);
						if (appBackends != null){
							rebuild(ar,appBackends);
						}						
					}
				}
			}			
			return ar.get(route);
		}

		@Override
		public List<Backend> select(String app, Properties p) {
			String route = getRoute(app,p);
			return select(app,route);
		}

		protected abstract String getRoute(String app, Properties p);

		@Override
		public synchronized void rebuild(AppBackends app) {
			if (app == null){
				routes.clear();
			}else{
				String id = app.getId();
				AppRoute ar = routes.get(id);
				if (ar == null){
					ar = new AppRoute();
					routes.put(id, ar);
				}else{
					ar.clear();
				}
				rebuild(ar,app);
			}
		}

		protected abstract void rebuild(AppRoute ar, AppBackends app);
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				super.report(xml);
				
				boolean detail = XmlTools.getBoolean(xml, "detail", false);
				if (detail){
					String app = XmlTools.getString(xml, "app", "");
					if (StringUtils.isNotEmpty(app)){
						AppRoute ar = routes.get(app);
						if (ar != null){
							ar.report(xml);
						}
					}
				}				
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				super.report(json);
				
				boolean detail = JsonTools.getBoolean(json, "detail", false);
				if (detail){
					String app = JsonTools.getString(json, "app", "");
					if (StringUtils.isNotEmpty(app)){
						AppRoute ar = routes.get(app);
						if (ar != null){
							ar.report(json);
						}
					}
				}				
			}
		}		
	}
}