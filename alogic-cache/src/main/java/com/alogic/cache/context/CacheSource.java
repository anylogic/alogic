package com.alogic.cache.context;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.cache.core.CacheStore;
import com.alogic.cache.core.MultiFieldObject;
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
 * @version 1.6.3.3 <br>
 * - 增加对象快速存取方法{@link #getObject(String, String, boolean)} <br>
 * 
 * @version 1.6.4.4 [20150910 duanyy] <br>
 * - 父类已经提供了current，淘汰掉 <br>
 * 
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 * 
 * @deprecated
 */
public class CacheSource extends Source<CacheStore> {
	private static final TheFactory factory = new TheFactory();
	private static CacheSource theInstance = null;
	@Override
	public Context<CacheStore> newInstance(Element e, Properties p,
			String attrName) {
		return factory.newInstance(e,p,attrName,InnerContext.class.getName());
	}
	
	@Override
	protected String getContextName(){
		return "context";
	}	
	
	/**
	 * 直接通过cacheId和对象id查找对象
	 * @param cacheId cache id
	 * @param id 对象id
	 * @param cacheAllowed 是否接受cache
	 * @return 对象实例
	 */
	public MultiFieldObject getObject(String cacheId,String id,boolean cacheAllowed){
		CacheStore store = get(cacheId);
		if (store != null){
			return store.load(id, cacheAllowed);
		}
		return null;
	}
	
	/**
	 * 工厂类
	 * @author duanyy
	 * 
	 */
	public static class TheFactory extends Factory<Context<CacheStore>>{
		
	}
	
	public static Context<CacheStore> newInstance(Element doc,Properties p){
		if (doc == null) 
			return null;
		return factory.newInstance(doc, p);
	}	
	
	
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
