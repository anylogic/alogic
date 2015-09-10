package com.alogic.lucene.context;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.lucene.core.Indexer;
import com.anysoft.context.Context;
import com.anysoft.context.Source;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * Indexer Source
 * 
 * @author duanyy
 * @since 1.6.4.1
 * @version 1.6.4.4 [20150910 duanyy] <br>
 * - 父类已经提供了current，淘汰掉 <br>
 */
public class IndexerSource extends Source<Indexer>{

	public Context<Indexer> newInstance(Element e, Properties p, String attrName) {
		return factory.newInstance(e,p,attrName,InnerContext.class.getName());
	}
	
	protected String getContextName(){
		return "context";
	}	
	
	/**
	 * 通过XML元素创建Context
	 * 
	 * @param doc XML Element
	 * @param p 环境变量
	 * @return Context
	 */
	public static Context<Indexer> newInstance(Element doc,Properties p){
		if (doc == null) return null;
		return factory.newInstance(doc, p);
	}	
	
	public static IndexerSource theInstance = null;
	public static IndexerSource get(){
		if (theInstance != null){
			return theInstance;
		}
		
		synchronized (factory){
			if (theInstance == null){
				theInstance = (IndexerSource)newInstance(Settings.get(), new IndexerSource());
			}
		}
		return theInstance;
	}	
	
	protected static Context<Indexer> newInstance(Properties p,Context<Indexer> instance){
		String configFile = p.GetValue("indexer.master", 
				"java:///com/alogic/lucene/context/indexer.xml#com.alogic.lucene.context.IndexerSource");

		String secondaryFile = p.GetValue("indexer.secondary", 
				"java:///com/alogic/lucene/context/indexer.xml#com.alogic.lucene.context.IndexerSource");
		
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
	 * 工厂类
	 * @author duanyy
	 * 
	 */
	public static class TheFactory extends Factory<Context<Indexer>>{
		
	}

	public static final TheFactory factory = new TheFactory();
}
