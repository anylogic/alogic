package com.alogic.sequence.context;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.sequence.core.SequenceGenerator;
import com.anysoft.context.Context;
import com.anysoft.context.Source;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 序列号来源
 * 
 * @author duanyy
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 * 
 * @deprecated
 */
public class SequenceSource extends Source<SequenceGenerator>{
	
	/**
	 * Factory
	 */
	private static final TheFactory factory = new TheFactory();
	
	private static SequenceSource theInstance = null;
	
	@Override
	public Context<SequenceGenerator> newInstance(Element e, Properties p,
			String attrName) {
		return factory.newInstance(e,p,attrName,XmlInner.class.getName());
	}
	
	@Override
	protected String getContextName(){
		return "context";
	}
	
	/**
	 * 工厂类
	 * @author duanyy
	 * 
	 */
	public static class TheFactory extends Factory<Context<SequenceGenerator>>{
		
	}
	
	public static Context<SequenceGenerator> newInstance(Element doc,Properties p){
		if (doc == null) 
			return null;
		return factory.newInstance(doc, p);
	}	
	
	public static SequenceSource get(){
		if (theInstance != null){
			return theInstance;
		}
		
		synchronized (factory){
			if (theInstance == null){
				theInstance = (SequenceSource)newInstance(Settings.get(), new SequenceSource());
			}
		}
		return theInstance;
	}	
	
	protected static Context<SequenceGenerator> newInstance(Properties p,Context<SequenceGenerator> instance){
		String configFile = p.GetValue("seq.master", 
				"java:///com/alogic/sequence/context/seq.xml#com.alogic.sequence.context.SequenceSource");

		String secondaryFile = p.GetValue("seq.secondary", 
				"java:///com/alogic/sequence/context/seq.xml#com.alogic.sequence.context.SequenceSource");
		
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
