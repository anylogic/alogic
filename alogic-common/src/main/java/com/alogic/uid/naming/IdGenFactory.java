package com.alogic.uid.naming;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.naming.Context;
import com.alogic.naming.Naming;
import com.alogic.uid.IdGenerator;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 工厂类
 * @author yyduan
 * @since 1.6.11.5
 */
public class IdGenFactory extends Naming<IdGenerator>{
	/**
	 * 缺省配置文件
	 */
	protected static final String DEFAULT 
		= "java:///com/alogic/uid/default.xml#" + IdGenFactory.class.getName();
	
	/**
	 * 唯一实例
	 */
	protected static IdGenFactory theInstance = null;

	@Override
	protected Context<IdGenerator> newInstance(Element e, Properties p,
			String attrName) {
		Factory<Context<IdGenerator>> f = new Factory<Context<IdGenerator>>();
		return f.newInstance(e,p,attrName,Inner.class.getName());
	}

	@Override
	protected String getContextName(){
		return "context";
	}
	
	/**
	 * 根据id获取IdGen
	 * @param id generator的id
	 * @return IdGen实例
	 */
	public static IdGenerator get(String id){
		IdGenFactory src = IdGenFactory.get();
		return src.lookup(id);
	}
	

	
	/**
	 * 获取唯一实例
	 * @return 唯一实例
	 */
	public static IdGenFactory get(){
		if (theInstance != null){
			return theInstance;
		}
		
		synchronized (IdGenFactory.class){
			if (theInstance == null){
				theInstance = (IdGenFactory)newInstance(Settings.get(), new IdGenFactory());
			}
		}
		
		return theInstance;
	}
	
	public static Context<IdGenerator> newInstance(Element doc,Properties p){
		if (doc == null) return null;
		Factory<Context<IdGenerator>> f = new Factory<Context<IdGenerator>>();
		return f.newInstance(doc, p);
	}
	
	protected static Context<IdGenerator> newInstance(Properties p,Context<IdGenerator> instance){
		String configFile = p.GetValue("uid.master",DEFAULT); 
		String secondaryFile = p.GetValue("uid.secondary", DEFAULT); 

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
