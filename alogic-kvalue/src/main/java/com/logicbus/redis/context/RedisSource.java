package com.logicbus.redis.context;

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



/**
 * Redis数据源
 * 
 * @author duanyy
 *
 * @version 1.0.0.1 [20141106 duanyy] <br>
 * - 将Context实现改为通用的配置环境实现. <br>
 */
public class RedisSource extends Source<RedisPool> implements RedisContext {

	
	public Context<RedisPool> newInstance(Element e, Properties p,String attrName) {
		return factory.newInstance(e,p,attrName,InnerContext.class.getName());
	}

	protected String getContextName(){
		return "context";
	}
	
	public static class TheFactory extends Factory<Context<RedisPool>>{
		
	}
	
	public static final TheFactory factory = new TheFactory();
	
	public static Context<RedisPool> newInstance(Element doc,Properties p){
		if (doc == null) return null;
		return factory.newInstance(doc, p);
	}
	
	public static RedisSource theInstance = null;
	public static RedisSource get(){
		if (theInstance != null){
			return theInstance;
		}
		
		synchronized (factory){
			if (theInstance == null){
				theInstance = (RedisSource)newInstance(Settings.get(), new RedisSource());
			}
		}
		
		return theInstance;
	}
	
	protected static Context<RedisPool> newInstance(Properties p,Context<RedisPool> instance){
		String configFile = p.GetValue("redis.master", 
				"java:///com/logicbus/redis/context/redis.source.default.xml#com.logicbus.redis.context.RedisSource");

		String secondaryFile = p.GetValue("redis.secondary", 
				"java:///com/logicbus/redis/context/redis.source.default.xml#com.logicbus.redis.context.RedisSource");
		
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

	public RedisPool getPool(String id) {
		return get(id);
	}
}
