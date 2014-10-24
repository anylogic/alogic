package com.logicbus.kvalue.context;

import java.io.InputStream;
import java.util.ArrayList;
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
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.Watcher;
import com.anysoft.util.WatcherHub;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;
import com.logicbus.kvalue.context.impl.Inner;
import com.logicbus.kvalue.core.Schema;

/**
 * KVDB数据源
 * 
 * @author duanyy
 *
 */
public class KValueSource implements KValueContext,Watcher<Schema>{
	
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(KValueSource.class);
	
	/**
	 * 缓存的Schema对象
	 */
	protected Hashtable<String,Schema> caches = new Hashtable<String,Schema>();
	
	/**
	 * 监听器HUB
	 */
	protected WatcherHub<Schema> watcherHub = new WatcherHub<Schema>();
	
	/**
	 * 来源
	 */
	protected List<KValueContext> sources = new ArrayList<KValueContext>();
	
	
	
	public void close() throws Exception {
		caches.clear();
		
		for (KValueContext s:sources){
			IOTools.close(s);
		}
		
		sources.clear();
	}

	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		Properties p = new XmlElementProperties(_e,_properties);
		
		NodeList children = XmlTools.getNodeListByPath(_e, "source");
				
		for (int i = 0 ; i < children.getLength() ; i ++){
			Node n = children.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element e = (Element)n;
			
			try {
				KValueContext source = factory.newInstance(e,p,"module",Inner.class.getName());
				if (source != null){
					source.addWatcher(this);
					sources.add(source);
				}
			}catch (Exception ex){
				logger.error("Can not create KVDBContext instance,check your configuration.");
			}
		}
	}

	
	public void report(Element xml) {
		
	}

	
	public void report(Map<String, Object> json) {
		
	}

	
	public void added(String id, Schema _data) {
		if (watcherHub != null){
			watcherHub.added(id, _data);
		}
	}

	
	public void removed(String id, Schema _data) {
		if (watcherHub != null){
			watcherHub.removed(id, _data);
		}
	}

	
	public void changed(String id, Schema _data) {
		if (watcherHub != null){
			watcherHub.changed(id, _data);
		}
	}

	
	public Schema getSchema(String id) {
		Schema found = caches.get(id);
		
		if (found == null){
			synchronized (caches){
				found = caches.get(id);
				if (found == null){
					found = loadSchema(id);
					if (found != null){
						caches.put(id, found);
					}
				}
			}
		}
		
		return found;
	}

	/**
	 * 从sources中搜索Schema
	 * 
	 * @param id
	 * @return
	 */
	protected Schema loadSchema(String id) {
		Schema found = null;
		for (KValueContext s:sources){
			found = s.getSchema(id);
			if (found != null){
				return found;
			}
		}
		return found;
	}

	
	public void addWatcher(Watcher<Schema> watcher) {
		watcherHub.addWatcher(watcher);
	}

	
	public void removeWatcher(Watcher<Schema> watcher) {
		watcherHub.removeWatcher(watcher);
	}

	/**
	 * 工厂类
	 * @author duanyy
	 *
	 */
	public static class TheFactory extends Factory<KValueContext>{
		
	}
	
	/**
	 * 工厂类
	 */
	public static final TheFactory factory = new TheFactory();
	
	/**
	 * 通过XML文档创建Context实例 
	 * @param root
	 * @param p
	 * @return
	 */
	public static KValueContext newInstance(Element root,Properties p){
		if (root == null){
			return null;
		}
		return factory.newInstance(root, p);
	}
	
	/**
	 * 创建Context实例
	 * @param p
	 * @param instance
	 * @return
	 */
	protected static KValueContext newInstance(Properties p,KValueContext instance){
		String configFile = p.GetValue("kvdb.master", 
				"java:///com/logicbus/kvalue/context/kvalue.default.xml#com.logicbus.kvalue.context.KVDBContext");

		String secondaryFile = p.GetValue("kvdb.secondary", 
				"java:///com/logicbus/kvalue/context/kvalue.default.xml#com.logicbus.kvalue.context.KVDBContext");
		
		ResourceFactory rm = Settings.getResourceFactory();
		InputStream in = null;
		try {
			in = rm.load(configFile,secondaryFile, null);
			Document doc = XmlTools.loadFromInputStream(in);
			if (doc != null){
				if (instance == null){
					return newInstance(doc.getDocumentElement(),p);
				}else{
					instance.configure(doc.getDocumentElement(), p);
					return instance;
				}
			}
		} catch (Exception ex){
			logger.error("Error occurs when load xml file,source=" + configFile, ex);
		}finally {
			IOTools.closeStream(in);
		}
		return null;
	}
	
	/**
	 * the only instance
	 */
	protected static KValueSource theInstance = null;
	
	/**
	 * to get the only instance
	 * @return
	 */
	public static KValueSource get(){
		if (theInstance == null){
			synchronized (factory){
				if (theInstance == null){
					theInstance = (KValueSource)newInstance(Settings.get(),new KValueSource());
				}
			}
		}
		return theInstance;
	}
}
