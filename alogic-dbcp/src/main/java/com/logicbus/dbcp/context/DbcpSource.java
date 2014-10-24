package com.logicbus.dbcp.context;

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
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.impl.InnerContext;

/**
 * DBCP来源
 * 
 * @author duanyy
 * 
 * @since 1.2.9
 * 
 * @version 1.2.9.1 [20141017 duanyy]
 * - 增加{@link #getPool(String)}
 * 
 * @version 1.2.9.3 [20141122 duanyy]
 * - getPool变更为static
 */
public class DbcpSource extends Source<ConnectionPool> {

	/**
	 * 通过ID来获取ConnectionPool
	 * 
	 * @param id
	 * @return
	 */
	public static ConnectionPool getPool(String id){
		return DbcpSource.get().get(id);
	}
	
	
	public Context<ConnectionPool> newInstance(Element e, Properties p,String attrName) {
		return factory.newInstance(e, p, attrName, InnerContext.class.getName());
	}

	protected String getContextName(){
		return "ds";
	}
	
	public static class TheFactory extends Factory<Context<ConnectionPool>>{
		
	}
	
	public static final TheFactory factory = new TheFactory();
	
	public static Context<ConnectionPool> newInstance(Element doc,Properties p){
		if (doc == null) return null;
		return factory.newInstance(doc, p);
	}
	
	public static DbcpSource theInstance = null;
	public static DbcpSource get(){
		if (theInstance != null){
			return theInstance;
		}
		
		synchronized (factory){
			if (theInstance == null){
				theInstance = (DbcpSource)newInstance(Settings.get(), new DbcpSource());
			}
		}
		
		return theInstance;
	}
	
	protected static Context<ConnectionPool> newInstance(Properties p,Context<ConnectionPool> instance){
		String configFile = p.GetValue("dbcp.master", 
				"java:///com/logicbus/dbcp/context/dbcp.context.default.xml#" + DbcpSource.class.getName());

		String secondaryFile = p.GetValue("dbcp.secondary", 
				"java:///com/logicbus/dbcp/context/dbcp.context.default.xml#" + DbcpSource.class.getName());
		
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
