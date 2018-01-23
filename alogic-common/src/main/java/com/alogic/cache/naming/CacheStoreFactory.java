package com.alogic.cache.naming;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.cache.CacheObject;
import com.alogic.load.Store;
import com.alogic.naming.Context;
import com.alogic.naming.Naming;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 工厂类
 * @author yyduan
 * @since 1.6.11.6
 * 
 * @version 1.6.11.8 [20180109] duanyy <br>
 * - 优化缓存相关的xscript插件 <br>
 */
public class CacheStoreFactory extends Naming<Store<CacheObject>>{
	/**
	 * 缺省配置文件
	 */
	protected static final String DEFAULT 
		= "java:///com/alogic/cache/default.xml#" + CacheStoreFactory.class.getName();
	
	/**
	 * 唯一实例
	 */
	protected static CacheStoreFactory theInstance = null;

	@Override
	protected Context<Store<CacheObject>> newInstance(Element e, Properties p,
			String attrName) {
		Factory<Context<Store<CacheObject>>> f = new Factory<Context<Store<CacheObject>>>();
		return f.newInstance(e,p,attrName,Inner.class.getName());
	}

	@Override
	protected String getContextName(){
		return "context";
	}
	
	/**
	 * 根据id获取Store
	 * @param id Store的id
	 * @return Store实例
	 */
	public static Store<CacheObject> get(String id){
		CacheStoreFactory src = CacheStoreFactory.get();
		return src.lookup(id);
	}

	/**
	 * 获取唯一实例
	 * @return 唯一实例
	 */
	public static CacheStoreFactory get(){
		if (theInstance != null){
			return theInstance;
		}
		
		synchronized (CacheStoreFactory.class){
			if (theInstance == null){
				theInstance = (CacheStoreFactory)newInstance(Settings.get(), new CacheStoreFactory());
			}
		}
		
		return theInstance;
	}
	
	public static Context<Store<CacheObject>> newInstance(Element doc,Properties p){
		if (doc == null) return null;
		Factory<Context<Store<CacheObject>>> f = new Factory<Context<Store<CacheObject>>>();
		return f.newInstance(doc, p);
	}
	
	protected static Context<Store<CacheObject>> newInstance(Properties p,Context<Store<CacheObject>> instance){
		String configFile = p.GetValue("cache.master",DEFAULT); 
		String secondaryFile = p.GetValue("cache.secondary", DEFAULT); 

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
			LOG.error("Error occurs when load xml file,source=" + configFile, ex);
		}finally {
			IOTools.closeStream(in);
		}
		return null;
	}	
}