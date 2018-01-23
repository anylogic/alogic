package com.alogic.blob.naming;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.blob.BlobManager;
import com.alogic.naming.Context;
import com.alogic.naming.Naming;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 工程类
 * @author yyduan
 *
 */
public class BlobManagerFactory extends Naming<BlobManager>{
	/**
	 * 缺省配置文件
	 */
	protected static final String DEFAULT 
		= "java:///com/alogic/blob/default.xml#" + BlobManagerFactory.class.getName();
	
	/**
	 * 唯一实例
	 */
	protected static BlobManagerFactory INSTANCE = null;
	
	@Override
	protected Context<BlobManager> newInstance(Element e, Properties p,
			String attrName) {
		Factory<Context<BlobManager>> f = new Factory<Context<BlobManager>>();
		return f.newInstance(e, p, "module", Inner.class.getName());
	}
	
	@Override
	protected String getContextName(){
		return "context";
	}
	
	public static BlobManager get(String id){
		BlobManagerFactory src = BlobManagerFactory.get();
		return src.lookup(id);
	}

	/**
	 * 获取唯一实例
	 * @return 唯一实例
	 */
	public static BlobManagerFactory get(){
		if (INSTANCE != null){
			return INSTANCE;
		}
		
		synchronized (BlobManagerFactory.class){
			if (INSTANCE == null){
				INSTANCE = (BlobManagerFactory)newInstance(Settings.get(), new BlobManagerFactory());
			}
		}
		
		return INSTANCE;
	}
	
	public static Context<BlobManager> newInstance(Element doc,Properties p){
		if (doc == null) return null;
		Factory<Context<BlobManager>> f = new Factory<Context<BlobManager>>();
		return f.newInstance(doc, p);
	}
	
	protected static Context<BlobManager> newInstance(Properties p,Context<BlobManager> instance){
		String configFile = p.GetValue("blob.master",DEFAULT); 
		String secondaryFile = p.GetValue("blob.secondary", DEFAULT); 

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
