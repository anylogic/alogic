package com.anysoft.metrics.core;

import java.io.InputStream;




import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.anysoft.stream.Handler;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 指标接口
 * 
 * @author duanyy
 * @since 1.2.8
 * 
 * @version 1.2.8.1 [20140919 duanyy] <br>
 * - getInstance拆分为getClientInstance和getServerInstance <br>
 * 
 * @version 1.6.4.16 [duanyy 20151110] <br>
 * - 根据sonar建议优化代码 <br>
 * 
 * @version 1.6.6.11 [duanyy 20161229] <br>
 * - 分别为服务端和客户端维护两个实例 <br>
 * 
 */
public interface MetricsHandler extends Handler<Fragment>,MetricsCollector{
	public static class TheFactory extends Factory<MetricsHandler>{
		/**
		 * a logger of log4j
		 */
		protected static final Logger LOG = LogManager.getLogger(TheFactory.class);

		/**
		 * 缺省配置文件
		 */
		public static final String DEFAULT = 
				"java:///com/anysoft/metrics/core/metrics.handler.default.xml#com.anysoft.metrics.core.MetricsHandler";

		/**
		 * 用于客户端的MetricsHandler
		 */
		private static MetricsHandler client = null;
		
		/**
		 * 用于服务端的MetricsHandler
		 */
		private static MetricsHandler server = null;
		
		/**
		 * 工厂实例
		 */
		protected static TheFactory INSTANCE = new TheFactory();
		
		/**
		 * 获取客户端的唯一实例
		 * @param p 环境变量
		 * @return client
		 */
		public static MetricsHandler getClientInstance(Properties p){
			if (client == null){
				synchronized (MetricsHandler.class){
					if (client == null){
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
		 * @param p 环境变量
		 * @return server
		 */
		public static MetricsHandler getServerInstance(Properties p){
			if (server == null){
				synchronized (MetricsHandler.class){
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
		protected static MetricsHandler getInstance(String master,String secondary,Properties props){			
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
		
		public static MetricsHandler getInstance(Element e,Properties p){
			return INSTANCE.newInstance(e, p);
		}
	}	
}
