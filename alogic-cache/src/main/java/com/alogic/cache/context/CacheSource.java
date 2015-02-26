package com.alogic.cache.context;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.cache.core.CacheStore;
import com.anysoft.context.Context;
import com.anysoft.context.Source;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * Cache配置来源
 * 
 * @author duanyy
 * @since 1.6.3.3
 * 
 */
public class CacheSource extends Source<CacheStore> {

	@Override
	public Context<CacheStore> newInstance(Element e, Properties p,
			String attrName) {
		return factory.newInstance(e,p,attrName,InnerContext.class.getName());
	}
	
	/**
	 * 获取当前的cache列表
	 * @return cache列表
	 */
	public CacheStore [] current(){
		return caches.values().toArray(new CacheStore[caches.size()]);
	}
	
	protected String getContextName(){
		return "context";
	}	
	
	/**
	 * 工厂类
	 * @author duanyy
	 * 
	 */
	public static class TheFactory extends Factory<Context<CacheStore>>{
		
	}
	
	public static final TheFactory factory = new TheFactory();
	
	public static Context<CacheStore> newInstance(Element doc,Properties p){
		if (doc == null) return null;
		return factory.newInstance(doc, p);
	}	
	
	public static CacheSource theInstance = null;
	public static CacheSource get(){
		if (theInstance != null){
			return theInstance;
		}
		
		synchronized (factory){
			if (theInstance == null){
				theInstance = (CacheSource)newInstance(Settings.get(), new CacheSource());
			}
		}
		return theInstance;
	}	
	
	protected static Context<CacheStore> newInstance(Properties p,Context<CacheStore> instance){
		String configFile = p.GetValue("cache.master", 
				"java:///com/alogic/cache/context/cache.xml#com.alogic.cache.context.CacheSource");

		String secondaryFile = p.GetValue("cache.secondary", 
				"java:///com/alogic/cache/context/cache.xml#com.alogic.cache.context.CacheSource");
		
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
}
