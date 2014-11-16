package com.anysoft.context;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.Watcher;
import com.anysoft.util.WatcherHub;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 通用配置来源
 * 
 * @author duanyy
 *
 * @param <object>
 * 
 * @since 1.5.0
 * 
 * @version 1.5.2 [20141017 duanyy] <br>
 * - 实现Reportable接口 <br>
 */
abstract public class Source<object extends Reportable> implements Context<object>,Watcher<object> {
	
	/**
	 * logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(Source.class);	
	
	/**
	 * Watcher Hub
	 */
	protected WatcherHub<object> watcherHub = new WatcherHub<object>();
	
	/**
	 * 缓存的对象
	 */
	protected Hashtable<String,object> caches = new Hashtable<String,object>();

	/**
	 * 配置来源
	 */
	protected List<Context<object>> sources = new ArrayList<Context<object>>();
	
	
	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		Properties p = new XmlElementProperties(_e,_properties);
		
		NodeList children = XmlTools.getNodeListByPath(_e, getContextName());
				
		for (int i = 0 ; i < children.getLength() ; i ++){
			Node n = children.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element e = (Element)n;
			
			try {
				Context<object> source = newInstance(e, p,"module");
				if (source != null){
					source.addWatcher(this);
					sources.add(source);
				}
			}catch (Exception ex){
				logger.error("Can not create context instance,check your configuration.");
			}
		}
	}
	

	/**
	 * 创建实例
	 * 
	 * @param e XML配置根节点
	 * @param p 环境变量集
	 * @param attrName XML属性名
	 * @return Context<object>
	 */
	abstract public Context<object> newInstance(Element e, Properties p, String attrName);
	
	protected String getContextName(){
		return "context";
	}

	
	public void close() throws Exception {
		caches.clear();
		
		for (Context<object> s:sources){
			s.removeWatcher(this);
			IOTools.close(s);
		}
		
		sources.clear();
	}

	
	public object get(String id) {
		object found = caches.get(id);
		if (found == null){
			synchronized (caches){
				found = caches.get(id);
				if (found == null){
					found = load(id);
					if (found != null){
						caches.put(id, found);
					}
				}
			}
		}
		return found;
	}

	private object load(String id) {
		for (Context<object> c:sources){
			object found = c.get(id);
			if (found != null){
				return found;
			}
		}
		return null;
	}

	
	public void addWatcher(Watcher<object> watcher) {
		watcherHub.addWatcher(watcher);
	}

	
	public void removeWatcher(Watcher<object> watcher) {
		watcherHub.removeWatcher(watcher);
	}

	
	public void added(String id, object _data) {
		if (watcherHub != null){
			watcherHub.added(id, _data);
		}
	}

	
	public void removed(String id, object _data) {
		caches.remove(id);
		if (watcherHub != null){
			watcherHub.removed(id, _data);
		}
	}

	
	public void changed(String id, object _data) {
		caches.remove(id);
		if (watcherHub != null){
			watcherHub.changed(id, _data);
		}
	}
	
	
	public void report(Element xml){
		if (xml != null){
			xml.setAttribute("module", getClass().getName());
			xml.setAttribute("ctxName", getContextName());
			
			Document doc = xml.getOwnerDocument();
			
			//contexts
			{
				Element _contexts = doc.createElement("contexts");
				
				for (Context<object> c:sources){
					Element _ctx = doc.createElement(getContextName());
					
					c.report(_ctx);
					
					_contexts.appendChild(_ctx);
				}
				
				xml.appendChild(_contexts);
			}
			
			//caches
			{
				Element _caches = doc.createElement("caches");
				
				Enumeration<String> _keys = caches.keys();
				
				while (_keys.hasMoreElements()){
					String key = _keys.nextElement();					
					Element _cache = doc.createElement("cache");
					_cache.setAttribute("id", key);
					_caches.appendChild(_cache);
				}
				
				xml.appendChild(_caches);
			}
		}
	}
	
	
	public void report(Map<String,Object> json){
		if (json != null){
			json.put("module", getClass().getName());
			json.put("ctxName", getContextName());
			
			//contexts
			{
				List<Object> _contexts = new ArrayList<Object>();
				
				for (Context<object> c:sources){
					Map<String,Object> _ctx = new HashMap<String,Object>();
					
					c.report(_ctx);
					
					_contexts.add(_ctx);
				}
				
				json.put(getContextName(), _contexts);
			}
			
			//caches
			{
				List<Object> _caches = new ArrayList<Object>();
				
				Enumeration<String> _keys = caches.keys();
				while (_keys.hasMoreElements()){
					String key = _keys.nextElement();
				
					Map<String,Object> _cache = new HashMap<String,Object>();
					_cache.put("id", key);
					_caches.add(_cache);
				}
				
				json.put("cache", _caches);
			}
		}
	}
}
