package com.alogic.rpc.naming;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.naming.Context;
import com.alogic.naming.Naming;
import com.alogic.rpc.Call;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * Call工厂
 * @author duanyy
 * @since 1.6.7.15
 */
public class CallFactory extends Naming<Call>{

	public Context<Call> newInstance(Element e, Properties p,String attrName) {
		return factory.newInstance(e,p,attrName,Inner.class.getName());
	}

	protected String getContextName(){
		return "context";
	}
	
	/**
	 * 直接获取Call对象
	 * 
	 * @param id
	 * @return Call对象
	 * 
	 */
	public static Call getCall(String id){
		CallFactory src = CallFactory.get();
		return src.lookup(id);
	}
	
	public static class TheFactory extends Factory<Context<Call>>{
		
	}
	
	protected static final TheFactory factory = new TheFactory();
	
	public static Context<Call> newInstance(Element doc,Properties p){
		if (doc == null) return null;
		return factory.newInstance(doc, p);
	}
	
	public static CallFactory theInstance = null;
	public static CallFactory get(){
		if (theInstance != null){
			return theInstance;
		}
		
		synchronized (factory){
			if (theInstance == null){
				theInstance = (CallFactory)newInstance(Settings.get(), new CallFactory());
			}
		}
		
		return theInstance;
	}
	
	protected static final String DEFAULT = "java:///com/alogic/rpc/context/context.default.xml#" + CallFactory.class.getName();
	
	protected static Context<Call> newInstance(Properties p,Context<Call> instance){
		String configFile = p.GetValue("rpc.master",DEFAULT); 
		String secondaryFile = p.GetValue("rpc.secondary", DEFAULT); 

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