package com.logicbus.backend.bizlog;

import java.io.InputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.stream.AbstractHandler;
import com.anysoft.stream.DispatchHandler;
import com.anysoft.stream.HubHandler;
import com.anysoft.stream.Handler;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 业务日志接口
 * 
 * @author duanyy
 * @since 1.2.3
 * 
 * @version 1.2.7 [20140828 duanyy] <br>
 * - 通过com.anysoft.stream来实现 <br>
 * 
 * @version 1.2.8 [20140917 duanyy] <br>
 * - Handler:handle和flush方法增加timestamp参数，以便时间同步 <br>
 * 
 * @version 1.6.3.16 [20150509 duanyy] <br>
 * - 修正备用bizlog.secondary变量名，笔误<br>
 */
public interface BizLogger extends Handler<BizLogItem>{	
	
	public static class Dispatch extends DispatchHandler<BizLogItem> implements BizLogger{
		public String getHandlerType(){
			return "logger";
		}
	}
	
	public static class Hub extends HubHandler<BizLogItem> implements BizLogger{
		public String getHandlerType(){
			return "logger";
		}
	}	
	
	public static class Default extends AbstractHandler<BizLogItem> implements BizLogger{

		
		protected void onHandle(BizLogItem _data,long t) {

		}

		
		protected void onFlush(long t) {

		}

		
		protected void onConfigure(Element e, Properties p) {

		}
		
	}
	
	/**
	 * 工厂类
	 * @author duanyy
	 *
	 */
	public static class TheFactory extends Factory<BizLogger>{
		/**
		 * a logger of log4j
		 */
		protected static final Logger logger = LogManager.getLogger(BizLogger.class);
		
		/**
		 * 根据环境变量中的配置来创建BizLogger
		 * @param props
		 * @return BizLogger实例
		 */
		public static BizLogger getLogger(Properties props){
			String master = props.GetValue("bizlog.master", 
					"java:///com/logicbus/backend/bizlog/bizlogger.default.xml#com.logicbus.backend.bizlog.BizLogger");
			String secondary = props.GetValue("bizlog.secondary", 
					"java:///com/logicbus/backend/bizlog/bizlogger.default.xml#com.logicbus.backend.bizlog.BizLogger");
			
			ResourceFactory rf = Settings.getResourceFactory();
			
			InputStream in = null;
			try {
				in = rf.load(master,secondary, null);
				Document doc = XmlTools.loadFromInputStream(in);		
				if (doc != null){
					return getLogger(doc.getDocumentElement(),props);
				}
			}catch (Throwable ex){
				logger.error("Error occurs when load xml file,source=" + master, ex);
			}finally {
				IOTools.closeStream(in);
			}
			return null;
		}
		
		protected static final TheFactory instance = new TheFactory();
		
		/**
		 * 从XML文档中创建BizLogger
		 * @param _e
		 * @param _p
		 * @return BizLogger实例
		 */
		public static BizLogger getLogger(Element _e,Properties _p) {
			return instance.newInstance(_e, _p,"module", Default.class.getName());
		}
	}	
}
