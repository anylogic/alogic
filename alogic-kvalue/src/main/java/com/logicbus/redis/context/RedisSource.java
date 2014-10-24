package com.logicbus.redis.context;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;

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
import com.logicbus.redis.client.Client;
import com.logicbus.redis.context.impl.Inner;
import com.logicbus.redis.toolkit.StringTool;


/**
 * Redis Context
 * @author duanyy
 *
 */
public class RedisSource implements RedisContext,Watcher<RedisPool>{
	/**
	 * logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(RedisSource.class);
	
	/**
	 * 缓存的对象
	 */
	protected Hashtable<String,RedisPool> caches = new Hashtable<String,RedisPool>();
	
	/**
	 * 配置来源
	 */
	protected List<RedisContext> sources = new ArrayList<RedisContext>();
	
	protected WatcherHub<RedisPool> watcherHub = new WatcherHub<RedisPool>();
	
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
				RedisContext source = factory.newInstance(e, p,"module",Inner.class.getName());
				if (source != null){
					source.addWatcher(this);
					sources.add(source);
				}
			}catch (Exception ex){
				logger.error("Can not create RedisPoolSource instance,check your configuration.");
			}
		}
	}

	protected RedisPool loadPool(String id){
		for (RedisContext s:sources){
			RedisPool found = s.getPool(id);
			if (found != null){
				return found;
			}
		}
		return null;
	}
	
	public RedisPool getPool(String id){
		RedisPool found = caches.get(id);
		if (found == null){
			synchronized (caches){
				found = caches.get(id);
				if (found == null){
					found = loadPool(id);
					if (found != null){
						caches.put(id, found);
					}
				}
			}
		}
		return found;
	}
	
	
	public void close() throws Exception {
		caches.clear();
		
		for (RedisContext s:sources){
			s.removeWatcher(this);
			IOTools.close(s);
		}
		
		sources.clear();
	}

	
	public void added(String id, RedisPool _data) {
		if (watcherHub != null){
			watcherHub.added(id, _data);
		}
	}

	
	public void removed(String id, RedisPool _data) {
		caches.remove(id);
		if (watcherHub != null){
			watcherHub.removed(id, _data);
		}
	}

	
	public void changed(String id, RedisPool _data) {
		caches.remove(id);
		if (watcherHub != null){
			watcherHub.changed(id, _data);
		}
	}
	
	public static class TheFactory extends Factory<RedisContext>{
		
	}
	
	public static final TheFactory factory = new TheFactory();
	
	public static RedisContext newInstance(Element doc,Properties p){
		if (doc == null) return null;
		return factory.newInstance(doc, p);
	}
	
	public static RedisSource theInstance = null;
	public static RedisSource get(){
		if (theInstance != null){
			return theInstance;
		}
		
		synchronized (factory){
			if (theInstance == null){
				theInstance = (RedisSource)newInstance(Settings.get(), new RedisSource());
			}
		}
		
		return theInstance;
	}
	
	protected static RedisContext newInstance(Properties p,RedisContext instance){
		String configFile = p.GetValue("redis.master", 
				"java:///com/logicbus/redis/context/redis.default.xml#com.logicbus.redis.context.RedisContext");

		String secondaryFile = p.GetValue("redis.secondary", 
				"java:///com/logicbus/redis/context/redis.default.xml#com.logicbus.redis.context.RedisContext");
		
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

	
	public void addWatcher(Watcher<RedisPool> watcher) {
		watcherHub.addWatcher(watcher);
	}

	
	public void removeWatcher(Watcher<RedisPool> watcher) {
		watcherHub.removeWatcher(watcher);
	}
	
	public static void main(String [] args){
		RedisSource context = RedisSource.get();
		
		Client client = null;
		RedisPool pool = context.getPool("default");
		if (pool != null){
			try {
				client = pool.getClient(3000);
				StringTool tool = (StringTool)client.getToolKit(StringTool.class);
				tool.set("th", "asdasd");	
			}catch (Exception ex){
				ex.printStackTrace();
			}finally{
				if (pool != null && client != null){
					pool.returnObject(client);
				}
				IOTools.close(context);
			}
		}
	}
}
