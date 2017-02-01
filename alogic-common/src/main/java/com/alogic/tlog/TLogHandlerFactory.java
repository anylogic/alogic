package com.alogic.tlog;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.metrics.stream.MetricsHandlerFactory;
import com.anysoft.stream.Handler;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 工厂类
 * @author yyduan
 * @since 1.6.7.3
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class TLogHandlerFactory extends Factory<Handler<TLog>>{
	
	/**
	 * a logger of log4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(TLogHandlerFactory.class);
	
	private TLogHandlerFactory(){
		
	}
	
	/**
	 * 缺省配置文件
	 */
	public static final String DEFAULT = 
			"java:///com/alogic/tlog/tlog.handler.default.xml#" + MetricsHandlerFactory.class.getName();

	/**
	 * 全局唯一的tlog handler
	 */
	private static Handler<TLog> client = null;
	
	/**
	 * 工厂实例
	 */
	protected static TLogHandlerFactory INSTANCE = new TLogHandlerFactory();	
	
	/**
	 * 根据环境变量中的配置来创建MetricsHandler
	 */
	protected static Handler<TLog> getInstance(String master,String secondary,Properties props){			
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
	
	public static Handler<TLog> getInstance(Element e,Properties p){
		return INSTANCE.newInstance(e, p);
	}	
	
	/**
	 * 获取客户端的唯一实例
	 * @return client
	 */
	public static Handler<TLog> getHandler(){
		if (client == null){
			synchronized (TLogHandlerFactory.class){
				if (client == null){
					Settings p = Settings.get();
					String master = p.GetValue("tlog.master",DEFAULT);
					String secondary = p.GetValue("tlog.secondary",DEFAULT);
					
					client = getInstance(master,secondary,p);						
				}
			}
		}
		return client;
	}	
}
