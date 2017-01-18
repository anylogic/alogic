package com.alogic.tlog.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.w3c.dom.Element;

import com.alogic.tlog.TLog;
import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.BaseException;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;

/**
 * log4j文件写出
 * @author yyduan
 *
 * @since 1.6.7.3
 */
public class Log4j extends AbstractHandler<TLog>{

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
	 * 行间隔符
	 */
	protected String eol = "$$";
		
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
		eol = PropertiesConstants.getString(p,"eol", eol);
		
		app = PropertiesConstants.getString(p, "app", "${server.app}");
		
		log4jProperties = new DefaultProperties("Default",Settings.get());
		log4jProperties.SetValue("thread", String.valueOf(thread));
		log4jProperties.SetValue("file",p.GetValue("log4j.file", "${tracelog.home}/tracelog_${server.app}_${server.port}_${thread}.log", false));
		log4jProperties.SetValue("datePattern",p.GetValue("log4j.datePattern", "'.'yyyy-MM-dd-HH-mm", false));
		log4jProperties.SetValue("encoding",p.GetValue("log4j.encoding", "${http.encoding}", false));
		log4jProperties.SetValue("bufferSize",p.GetValue("log4j.bufferSize", "10240", false));
		log4jProperties.SetValue("bufferedIO",p.GetValue("log4j.bufferedIO", "true", false));
		log4jProperties.SetValue("immediateFlush",p.GetValue("log4j.immediateFlush", "false", false));
		
	}

	protected void onHandle(TLog item,long t) {
		if (logger == null){
			synchronized (this){
				host = log4jProperties.GetValue("host", "${server.ip}:${server.port}");
				logger = initLogger(log4jProperties);
			}
		}		
		buf.setLength(0);

		String reason= item.reason();
		if (StringUtils.isEmpty(reason)){
			reason = "";
		}else{
			reason = StringUtils.replacePattern(reason, "\r\n", " "); //windows
			reason = StringUtils.replacePattern(reason, "\n", " ");//linux
			reason = StringUtils.replacePattern(reason, "\r", " ");//unix
			reason = StringUtils.replacePattern(reason, "\\|", "&brvbar;");
		}
		buf.append(item.sn()).append(delimeter)
		.append(item.order()).append(delimeter)
		.append(app).append(delimeter)
		.append(host).append(delimeter)
		.append(item.type()).append(delimeter)
		.append(item.method()).append(delimeter)
		.append(item.startDate()).append(delimeter)
		.append(item.duration()).append(delimeter)
		.append(item.contentLength()).append(delimeter)
		.append(item.code()).append(delimeter)
		.append(reason).append(eol);
		
		logger.info(buf.toString());
	}

	private Logger initLogger(Properties props) {
		Logger _logger = LogManager.getLogger(TLog.class.getName() + "." + thread);
		
		//允许输出到控制台
		_logger.setAdditivity(false);
		
		DailyRollingFileAppender myAppender = new DailyRollingFileAppender();
		myAppender.setFile(PropertiesConstants.getString(props,
				"file",
				"${tlog.home}/tlog_${server.app}_${server.port}_${thread}.log",true));
		myAppender.setDatePattern(PropertiesConstants.getString(props,
				"datePattern", "'.'yyyy-MM-dd-HH-mm",true));
		myAppender.setEncoding(PropertiesConstants.getString(props,
				"encoding", "${http.encoding}",true));
		//log4j默认的缓存块大小为8K。清空缓存规则如下，例一设置4K，还是要等到缓存达到8k才清空缓存；例二，设置10k，log4j是等到16K才刷出
		myAppender.setBufferSize(PropertiesConstants.getInt(props,
				"bufferSize", 10240,true));
		myAppender.setBufferedIO(PropertiesConstants.getBoolean(props,
				"bufferedIO", false,true));
		myAppender.setImmediateFlush(PropertiesConstants.getBoolean(props,
				"immediateFlush", false,true));
		myAppender.setLayout(new MyLayout());
		myAppender.setName(TLog.class.getName() + "." + thread);
	
		myAppender.activateOptions();
		_logger.addAppender(myAppender);
		_logger.setLevel(Level.INFO);
		return _logger;
	}

	
	protected void onFlush(long t) {
		//no buffer
	}

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
}
