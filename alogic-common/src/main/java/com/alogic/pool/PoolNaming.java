package com.alogic.pool;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.alogic.naming.Context;
import com.alogic.naming.Naming;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;


/**
 * 基于Pool的命名服务
 * 
 * @author duanyy
 *
 */
public class PoolNaming extends Naming<Pool>{
	/**
	 * 单一实例
	 */
	private static PoolNaming instance = null;

	/**
	 * 缺省的配置文件地址
	 */
	private static final String dftXrc = "java:///com/alogic/pool/naming.pool.default.xml#" + PoolNaming.class.getName();
	
	/**
	 * 构造函数
	 */
	protected PoolNaming(){
		
	}
	
	@Override
	protected Context<Pool> newInstance(Element e, Properties p, String attrName) {
		Factory<Context<Pool>> factory = new Factory<Context<Pool>>();
		return factory.newInstance(e, p, attrName);
	}
	
	
	public static PoolNaming get(){
		if (instance == null){
			synchronized(PoolNaming.class){
				if (instance == null){
					instance = (PoolNaming) newInstance(Settings.get(),new PoolNaming());
				}
			}
		}
		return instance;
	}
	
	protected static Context<Pool> newInstance(Properties p,Context<Pool> instance){
		String configFile = p.GetValue("naming.master",dftXrc);
		String secondaryFile = p.GetValue("naming.secondary",dftXrc);
		
		Document doc = loadDocument(configFile,secondaryFile);
		if (doc != null){
			if (instance == null){
				Factory<Context<Pool>> factory = new Factory<Context<Pool>>();
				return factory.newInstance(doc.getDocumentElement(), p, "module");
			}else{
				instance.configure(doc.getDocumentElement(), p);
				return instance;
			}
		}
		return null;
	}	
}
