package com.alogic.metrics.stream;

import java.io.InputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.metrics.Fragment;
import com.anysoft.stream.Handler;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 指标处理器工厂
 * 
 * @author yyduan
 *
 * @since 1.6.6.13
 *
 */
public class MetricsHandlerFactory extends Factory<Handler<Fragment>>{
	
	/**
	 * a logger of log4j
	 */
	protected static final Logger LOG = LogManager.getLogger(MetricsHandlerFactory.class);
	
	private MetricsHandlerFactory(){
		
	}
	
	/**
	 * 缺省配置文件
	 */
	public static final String DEFAULT = 
			"java:///com/alogic/metrics/metrics.handler.default.xml#" + MetricsHandlerFactory.class.getName();

	/**
	 * 用于客户端的MetricsHandler
	 */
	private static Handler<Fragment> client = null;
	
	/**
	 * 用于服务端的MetricsHandler
	 */
	private static Handler<Fragment> server = null;
	
	/**
	 * 工厂实例
	 */
	protected static MetricsHandlerFactory INSTANCE = new MetricsHandlerFactory();
	
	/**
	 * 获取客户端的唯一实例
	 * @return client
	 */
	public static Handler<Fragment> getClientInstance(){
		if (client == null){
			synchronized (MetricsHandlerFactory.class){
				if (client == null){
					Settings p = Settings.get();
					String master = p.GetValue("metrics.handler.client.master",DEFAULT);
					String secondary = p.GetValue("metrics.handler.client.secondary",DEFAULT);
					
					client = getInstance(master,secondary,p);						
				}
			}
		}
		return client;
	}
	
	/**
	 * 获取服务端的唯一实例
	 * @return server
	 */
	public static Handler<Fragment> getServerInstance(){
		if (server == null){
			synchronized (MetricsHandlerFactory.class){
				Settings p = Settings.get();
				String master = p.GetValue("metrics.handler.server.master",DEFAULT);
				String secondary = p.GetValue("metrics.handler.server.secondary",DEFAULT);
				
				server = getInstance(master,secondary,p);
			}
		}
		return server;
	}
	
	/**
	 * 根据环境变量中的配置来创建MetricsHandler
	 */
	protected static Handler<Fragment> getInstance(String master,String secondary,Properties props){			
		ResourceFactory rf = Settings.getResourceFactory();
		InputStream in = null;
		try {
			in = rf.load(master,secondary, null);
			Document doc = XmlTools.loadFromInputStream(in);		
			if (doc != null){
				return getInstance(doc.getDocumentElement(),props);
			}
		}catch (Exception ex){
			LOG.error("Error occurs when load xml file,source=" + master, ex);
		}finally {
			IOTools.closeStream(in);
		}
		return null;
	}		
	
	public static Handler<Fragment> getInstance(Element e,Properties p){
		return INSTANCE.newInstance(e, p);
	}	
}
