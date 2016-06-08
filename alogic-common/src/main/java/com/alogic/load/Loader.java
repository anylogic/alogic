package com.alogic.load;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Configurable;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.Reportable;
import com.anysoft.util.Watcher;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 通用装载框架
 * 
 * @author duanyy
 *
 */
public interface Loader<O extends Loadable> extends Configurable,XMLConfigurable,Reportable{
	
	/**
	 * 按照id装载指定的对象
	 * 
	 * @param id 对象id
	 * @param cacheAllowed 是否允许缓存
	 * @return 对象实例
	 */
	public O load(String id,boolean cacheAllowed);
	
	/**
	 * 注册监听器
	 * 
	 * @param watcher 监听器
	 */
	public void addWatcher(Watcher<O> watcher);
	
	/**
	 * 注销监听器
	 * @param watcher 监听器
	 */
	public void removeWatcher(Watcher<O> watcher);
	
	/**
	 * 虚基类 
	 * 
	 * @author duanyy
	 *
	 * @param <O>
	 */
	public abstract static class Abstract<O extends Loadable> implements Loader<O>{
		/**
		 * a logger of log4j
		 */
		protected static final Logger LOG = LogManager.getLogger(Loader.class);
		
		@Override
		public void configure(Properties p) {
			// nothing to do
		}
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}

		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml,"module",getClass().getName());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
			}
		}

		@Override
		public void addWatcher(Watcher<O> watcher) {
			// nothing to do
		}
		
		@Override
		public void removeWatcher(Watcher<O> watcher) {
			// nothing to do
		}
	}
	
	/**
	 * Sinkable
	 * @author duanyy
	 *
	 */
	public abstract static class Sinkable<O extends Loadable> extends Abstract<O> {
		protected List<Loader<O>> loaders = new ArrayList<Loader<O>>();
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
			
			NodeList nodeList = XmlTools.getNodeListByPath(e, "sink");
			Factory<Loader<O>> factory = new Factory<Loader<O>>();
			
			for (int i = 0 ;i < nodeList.getLength() ; i ++){
				Node n = nodeList.item(i);
				
				if (Node.ELEMENT_NODE != n.getNodeType()){
					continue;
				}
				
				Element elem = (Element)n;
				
				try {
					Loader<O> loader = factory.newInstance(elem, props, "module");
					if (loader != null){
						loaders.add(loader);
					}
				}catch (Exception ex){
					LOG.error("Can not create loader from element:" + XmlTools.node2String(elem));
				}
			}
			
		}
		
		@Override
		public O load(String id, boolean cacheAllowed) {
			O found = loadFromSelf(id,cacheAllowed);
			if (found == null){
				found = loadFromSink(id,cacheAllowed);
			}
			
			return found;
		}
	
		protected O loadFromSink(String id,boolean cacheAllowed){
			for (Loader<O> l:loaders){
				O found = l.load(id, cacheAllowed);
				if (found != null){
					return found;
				}
			}
			return null;
		}
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				super.report(xml);
				
				if (!loaders.isEmpty()){
					Document doc = xml.getOwnerDocument();
					for (Loader<O> l:loaders){
						Element elem = doc.createElement("sink");
						l.report(elem);
						xml.appendChild(elem);
					}
				}
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				super.report(json);
				
				if (!loaders.isEmpty()){
					List<Object> list = new ArrayList<Object>();
					for (Loader<O> l:loaders){
						Map<String,Object> map = new HashMap<String,Object>();
						l.report(map);
						list.add(map);
					}
					json.put("sink", list);
				}
			}
		}		
		
		protected abstract O loadFromSelf(String id,boolean cacheAllowed);
	}
	
	/**
	 * 缓存过的
	 * @author duanyy
	 *
	 * @param <O>
	 */
	public static class Cached<O extends Loadable> extends Sinkable<O>{
		/**
		 * 缓存的对象
		 */
		private Map<String,O> cachedObjects = new ConcurrentHashMap<String,O>();
		
		@Override
		public O load(String id, boolean cacheAllowed) {
			O found = loadFromSelf(id,cacheAllowed);
			if (found == null){
				synchronized (this){
					found = loadFromSelf(id,cacheAllowed);
					if (found == null){
						found = loadFromSink(id,cacheAllowed);
						if (found != null){
							cachedObjects.put(id, found);
						}
					}
				}
			}
			
			return found;
		}
		
		@Override
		protected O loadFromSelf(String id, boolean cacheAllowed) {
			return cacheAllowed?cachedObjects.get(id):null;
		}	
	}
	
	/**
	 * 容器
	 * 
	 * @author duanyy
	 *
	 * @param <O>
	 */
	public static class Container<O extends Loadable> extends Sinkable<O>{

		private Map<String,O> objects = new ConcurrentHashMap<String,O>();
		
		public void add(String id,O o){
			objects.put(id, o);
		}
		
		public void remove(String id){
			objects.remove(id);
		}
		
		public void clear(){
			objects.clear();
		}
		
		@Override
		protected O loadFromSelf(String id, boolean cacheAllowed) {
			return objects.get(id);
		}	
	}
	
	/**
	 * 基于XML配置的容器
	 * 
	 * @author duanyy
	 *
	 * @param <O>
	 */
	public abstract static class XmlResource<O extends Loadable>  extends Container<O>{
		protected abstract String getObjectXmlTag();
		protected abstract String getObjectDftClass();
		
		@Override
		public void configure(Element root, Properties p) {
			super.configure(root, p);
			
			NodeList nodeList = XmlTools.getNodeListByPath(root, getObjectXmlTag());
			Factory<O> factory = new Factory<O>();
			
			for (int i = 0 ;i < nodeList.getLength() ; i ++){
				Node n = nodeList.item(i);
				
				if (Node.ELEMENT_NODE != n.getNodeType()){
					continue;
				}
				
				Element e = (Element)n;
				O instance = factory.newInstance(e, p, "module", getObjectDftClass());				
				if (StringUtils.isNotEmpty(instance.getId())){
					add(instance.getId(), instance);
				}
			}
		}		
	}
	
	/**
	 * 管理器
	 * 
	 * @author duanyy
	 *
	 * @param <O>
	 */
	public static class Manager<O extends Loadable> extends Cached<O>{
		/**
		 * 通过XML Document来配置
		 * @param doc XML文档
		 * @param p 变量集
		 */
		protected void configure(Document doc,Properties p){
			if (doc != null && doc.getDocumentElement() != null){
				configure(doc.getDocumentElement(),p);
			}
		}
		
		/**
		 * 通过一个XML资源来配置
		 * 
		 * @param src XML资源地址
		 */
		protected void configure(String src){
			configure(src,null);
		}
		
		/**
		 * 通过XML资源来配置
		 * @param src XML资源地址
		 * @param secondary 备用资源地址
		 */
		protected void configure(String src,String secondary){
			ResourceFactory rf = Settings.getResourceFactory();
			InputStream in = null;
			try {
				in = rf.load(src, secondary, null);
				Document doc = XmlTools.loadFromInputStream(in);
				configure(doc,Settings.get());
			} catch (Exception ex) {
				LOG.error("Error occurs when load xml file,source=" + src, ex);
			} finally {
				IOTools.closeStream(in);
			}			
		}
	}
}
