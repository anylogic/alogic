package com.alogic.remote.naming;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.naming.Context;
import com.alogic.naming.Naming;
import com.alogic.remote.Client;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * Client 工厂
 * @author yyduan
 *
 * @since 1.6.10.3
 */
public class ClientFactory extends Naming<Client>{

	@Override
	protected Context<Client> newInstance(Element e, Properties p,
			String attrName) {
		return factory.newInstance(e,p,attrName,Inner.class.getName());
	}

	protected String getContextName(){
		return "context";
	}
	
	/**
	 * 直接获取Client对象
	 * 
	 * @param id
	 * @return Client对象
	 * 
	 */
	public static Client getCall(String id){
		ClientFactory src = ClientFactory.get();
		return src.lookup(id);
	}
	
	public static class TheFactory extends Factory<Context<Client>>{
		
	}
	
	protected static final TheFactory factory = new TheFactory();
	
	public static Context<Client> newInstance(Element doc,Properties p){
		if (doc == null) return null;
		return factory.newInstance(doc, p);
	}
	
	private static ClientFactory theInstance = null;
	public static ClientFactory get(){
		if (theInstance != null){
			return theInstance;
		}
		
		synchronized (factory){
			if (theInstance == null){
				theInstance = (ClientFactory)newInstance(Settings.get(), new ClientFactory());
			}
		}
		
		return theInstance;
	}
	
	protected static final String DEFAULT = "java:///com/alogic/remote/naming/default.context.xml#" + ClientFactory.class.getName();
	
	protected static Context<Client> newInstance(Properties p,Context<Client> instance){
		String configFile = p.GetValue("remote.master",DEFAULT); 
		String secondaryFile = p.GetValue("remote.secondary", DEFAULT); 

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
