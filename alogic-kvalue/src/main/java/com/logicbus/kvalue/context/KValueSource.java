package com.logicbus.kvalue.context;

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
import com.logicbus.kvalue.core.Schema;


/**
 * KVDB数据源
 * 
 * @author duanyy
 * 
 * @version 1.0.0.1 [20141106 duanyy] <br>
 * - 将Context实现改为通用的配置环境实现. <br>
 *
 */
public class KValueSource extends Source<Schema> {

	public static Schema getSchema(String id){
		KValueSource _source = get();
		return _source.get(id);
	}
	
	public Context<Schema> newInstance(Element e, Properties p,String attrName) {
		return factory.newInstance(e,p,attrName,InnerContext.class.getName());
	}

	protected String getContextName(){
		return "context";
	}
	
	public static class TheFactory extends Factory<Context<Schema>>{
		
	}
	
	public static final TheFactory factory = new TheFactory();
	
	public static Context<Schema> newInstance(Element doc,Properties p){
		if (doc == null) return null;
		return factory.newInstance(doc, p);
	}
	
	public static KValueSource theInstance = null;
	public static KValueSource get(){
		if (theInstance != null){
			return theInstance;
		}
		
		synchronized (factory){
			if (theInstance == null){
				theInstance = (KValueSource)newInstance(Settings.get(), new KValueSource());
			}
		}
		
		return theInstance;
	}
	
	protected static Context<Schema> newInstance(Properties p,Context<Schema> instance){
		String configFile = p.GetValue("kvalue.master", 
				"java:///com/logicbus/kvalue/context/kvalue.source.default.xml#com.logicbus.kvalue.context.KValueSource");

		String secondaryFile = p.GetValue("kvalue.secondary", 
				"java:///com/logicbus/kvalue/context/kvalue.source.default.xml#com.logicbus.kvalue.context.KValueSource");
		
		ResourceFactory rm = Settings.getResourceFactory();
		InputStream in = null;
		try {
			logger.info("Load redis context from " + configFile);
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
