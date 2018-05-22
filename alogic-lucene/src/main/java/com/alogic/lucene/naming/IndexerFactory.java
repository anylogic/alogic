package com.alogic.lucene.naming;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.lucene.core.Indexer;
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
 * 
 * @author yyduan
 * @since 1.6.11.31
 */
public class IndexerFactory extends Naming<Indexer>{
	/**
	 * 缺省配置文件
	 */
	protected static final String DEFAULT 
		= "java:///com/alogic/lucene/default.xml#" + IndexerFactory.class.getName();
	
	/**
	 * 唯一实例
	 */
	protected static IndexerFactory theInstance = null;
	
	@Override
	protected Context<Indexer> newInstance(Element e, Properties p,
			String attrName) {
		Factory<Context<Indexer>> f = new Factory<Context<Indexer>>();
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
	public static Indexer get(String id){
		IndexerFactory src = IndexerFactory.get();
		return src.lookup(id);
	}

	/**
	 * 获取唯一实例
	 * @return 唯一实例
	 */
	public static IndexerFactory get(){
		if (theInstance != null){
			return theInstance;
		}
		
		synchronized (IndexerFactory.class){
			if (theInstance == null){
				theInstance = (IndexerFactory)newInstance(Settings.get(), new IndexerFactory());
			}
		}
		
		return theInstance;
	}
	
	public static Context<Indexer> newInstance(Element doc,Properties p){
		if (doc == null) return null;
		Factory<Context<Indexer>> f = new Factory<Context<Indexer>>();
		return f.newInstance(doc, p);
	}
	
	protected static Context<Indexer> newInstance(Properties p,Context<Indexer> instance){
		String configFile = p.GetValue("lucene.master",DEFAULT); 
		String secondaryFile = p.GetValue("lucene.secondary", DEFAULT); 

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
