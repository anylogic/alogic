package com.alogic.blob.context;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.blob.core.BlobManager;
import com.anysoft.context.Context;
import com.anysoft.context.Source;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * Blob管理器配置源
 * 
 * @author duanyy
 * @since 1.6.3.28
 */
public class BlobManagerSource extends Source<BlobManager> {

	@Override
	public Context<BlobManager> newInstance(Element e, Properties p,
			String attrName) {
		return factory.newInstance(e,p,attrName,XmlInner.class.getName());
	}

	protected String getContextName(){
		return "context";
	}
	
	
	public static final TheFactory factory = new TheFactory();
	
	/**
	 * 工厂类
	 * @author duanyy
	 * 
	 */
	public static class TheFactory extends Factory<Context<BlobManager>>{
		
	}
	
	public static Context<BlobManager> newInstance(Element doc,Properties p){
		if (doc == null) return null;
		return factory.newInstance(doc, p);
	}	
	
	public static BlobManagerSource theInstance = null;
	public static BlobManagerSource get(){
		if (theInstance != null){
			return theInstance;
		}
		
		synchronized (factory){
			if (theInstance == null){
				theInstance = (BlobManagerSource)newInstance(Settings.get(), new BlobManagerSource());
			}
		}
		return theInstance;
	}	
	
	protected static Context<BlobManager> newInstance(Properties p,Context<BlobManager> instance){
		String configFile = p.GetValue("blob.master", 
				"java:///com/alogic/blob/context/blob.xml#com.alogic.blob.context.BlobManagerSource");

		String secondaryFile = p.GetValue("blob.secondary", 
				"java:///com/alogic/blob/context/blob.xml#com.alogic.blob.context.BlobManagerSource");
		
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
