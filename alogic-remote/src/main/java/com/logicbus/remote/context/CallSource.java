package com.logicbus.remote.context;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.context.Context;
import com.anysoft.context.Source;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;
import com.logicbus.remote.core.Call;


/**
 * 远程调用的Source
 * @author duanyy
 *
 * @since 1.2.9
 * 
 * @version 1.2.9.3 [20141022 duanyy]
 * - 增加{@link #getCall(String)}
 */
public class CallSource extends Source<Call> {

	
	public Context<Call> newInstance(Element e, Properties p,String attrName) {
		return factory.newInstance(e,p,attrName,InnerContext.class.getName());
	}

	protected String getContextName(){
		return "context";
	}
	
	/**
	 * 直接获取Call对象
	 * 
	 * @param id
	 * @return
	 * 
	 * @since 1.2.9.3
	 * 
	 */
	public static Call getCall(String id){
		CallSource src = CallSource.get();
		return src.get(id);
	}
	
	public static class TheFactory extends Factory<Context<Call>>{
		
	}
	
	public static final TheFactory factory = new TheFactory();
	
	public static Context<Call> newInstance(Element doc,Properties p){
		if (doc == null) return null;
		return factory.newInstance(doc, p);
	}
	
	public static CallSource theInstance = null;
	public static CallSource get(){
		if (theInstance != null){
			return theInstance;
		}
		
		synchronized (factory){
			if (theInstance == null){
				theInstance = (CallSource)newInstance(Settings.get(), new CallSource());
			}
		}
		
		return theInstance;
	}
	
	protected static Context<Call> newInstance(Properties p,Context<Call> instance){
		String configFile = p.GetValue("call.master", 
				"java:///com/logicbus/remote/context/call.context.default.xml#com.logicbus.remote.context.CallSource");

		String secondaryFile = p.GetValue("call.secondary", 
				"java:///com/logicbus/remote/context/call.context.default.xml#com.logicbus.remote.context.CallSource");
		
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
