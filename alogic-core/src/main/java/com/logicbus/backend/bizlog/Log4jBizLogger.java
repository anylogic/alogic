package com.logicbus.backend.bizlog;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.w3c.dom.Element;

import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.BaseException;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;

/**
 * 基于Log4j的BizLogger
 * @author duanyy
 * @version 1.2.7 [20140828 duanyy] <br>
 * - 重写BizLogger
 * 
 * @version 1.2.7.1 [20140902 duanyy] <br>
 * - 增加app信息的输出
 * 
 * @version 1.2.8 [20140917 duanyy] <br>
 * - Handler:handle和flush方法增加timestamp参数，以便时间同步
 */
public class Log4jBizLogger extends AbstractHandler<BizLogItem> implements BizLogger {

	/**
	 * a logger of log4j
	 */
	protected Logger logger = null;
	
	/**
	 * 线程id
	 */
	protected int thread = 0;
	
	/**
	 * 输出间隔符
	 */
	protected String delimeter = "%%";
	
	/**
	 * 计费标志
	 */
	protected boolean isBilling = true;
	
	/**
	 * 单条记录的缓存
	 */
	protected StringBuffer buf = new StringBuffer();
	
	/**
	 * log4j的变量集模板
	 */
	protected DefaultProperties log4jProperties = null;
	
	/**
	 * 应用
	 * 
	 * @since 1.2.7.1
	 */
	protected String app;
	
	/**
	 * 服务主机(ip:port)
	 * 
	 * @since 1.2.7.1
	 */
	protected String host;
	
	
	protected void onConfigure(Element _e, Properties p) throws BaseException {
		thread = PropertiesConstants.getInt(p, "thread", 0);
		delimeter = PropertiesConstants.getString(p,"delimeter", delimeter);
		isBilling = PropertiesConstants.getBoolean(p,"billing", isBilling);
		app = PropertiesConstants.getString(p, "app", "${server.app}");
		
		log4jProperties = new DefaultProperties("Default",Settings.get());
		log4jProperties.SetValue("thread", String.valueOf(thread));
		log4jProperties.SetValue("file",p.GetValue("log4j.file", "${bizlog.home}/bizlog${server.port}_${thread}.log", false));
		log4jProperties.SetValue("datePattern",p.GetValue("log4j.datePattern", "'.'yyyy-MM-dd-HH-mm", false));
		log4jProperties.SetValue("encoding",p.GetValue("log4j.encoding", "${http.encoding}", false));
		log4jProperties.SetValue("bufferSize",p.GetValue("log4j.bufferSize", "10240", false));
		log4jProperties.SetValue("bufferedIO",p.GetValue("log4j.bufferedIO", "true", false));
		log4jProperties.SetValue("immediateFlush",p.GetValue("log4j.immediateFlush", "false", false));
	}

	
	protected void onHandle(BizLogItem item,long t) {
		if (logger == null){
			synchronized (this){
				host = log4jProperties.GetValue("host", "${server.host}:${server.port}");
				logger = initLogger(log4jProperties);
			}
		}		
		buf.setLength(0);
		
		buf.append(isBilling?1:0).append(delimeter)
		.append(item.sn).append(delimeter)
		.append(item.startTime).append(delimeter)
		.append(app).append(delimeter)
		.append(host).append(delimeter)
		.append(item.clientIP).append(delimeter)
		.append(item.client).append(delimeter)
		.append(item.duration).append(delimeter)
		.append(item.id).append(delimeter)
		.append(item.result).append(delimeter)
		.append(item.result.equals("core.ok")?"":item.reason).append(delimeter)
		.append(item.url).append(delimeter);
		
		if (item.content != null && item.content.length() > 0){
			buf.append(item.content.replaceAll("\n", "").replaceAll("\r",""));
		}
		logger.info(buf.toString());
	}

	private Logger initLogger(Properties props) {
		Logger _logger = LogManager.getLogger(Log4jBizLogger.class.getName() + "." + thread);
		_logger.setAdditivity(false);
		
		DailyRollingFileAppender myAppender = new DailyRollingFileAppender();
		myAppender.setFile(PropertiesConstants.getString(props,
				"file",
				"${bizlog.home}/bizlog${server.port}_${thread}.log",true));
		myAppender.setDatePattern(PropertiesConstants.getString(props,
				"datePattern", "'.'yyyy-MM-dd-HH-mm",true));
		myAppender.setEncoding(PropertiesConstants.getString(props,
				"encoding", "${http.encoding}",true));
		myAppender.setBufferSize(PropertiesConstants.getInt(props,
				"bufferSize", 10240,true));
		myAppender.setBufferedIO(PropertiesConstants.getBoolean(props,
				"bufferedIO", true,true));
		myAppender.setImmediateFlush(PropertiesConstants.getBoolean(props,
				"immediateFlush", false,true));
		myAppender.setLayout(new MyLayout());
		myAppender.setName(Log4jBizLogger.class.getName() + "." + thread);
		
		myAppender.activateOptions();
		_logger.addAppender(myAppender);
		
		return _logger;
	}

	
	protected void onFlush(long t) {
		//no buffer
	}
	
	/**
	 * 自定义的Layout
	 * 
	 * <br>
	 * BizLog输出格式固定，因此自定义一个Layout提高效率。
	 * 
	 * @author duanyy
	 *
	 * @since 1.2.3
	 */
	public static class MyLayout extends Layout{
		protected static String lineSeperator = System.getProperty("line.separator");
		
		public void activateOptions() {
		}

		
		public String format(LoggingEvent e) {
			return e.getRenderedMessage() + lineSeperator;
		}

		
		public boolean ignoresThrowable() {
			return true;
		}
	}
	
	public static void main(String [] args){
		System.out.println(BizLogger.Dispatch.class.getName());
	}
}
