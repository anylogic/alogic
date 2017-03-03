package com.alogic.tlog.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.alogic.tlog.TLog;
import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;

/**
 * log4j文件写出
 * @author yyduan
 *
 * @since 1.6.7.3
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.7.10 [20170202 duanyy] <br>
 * - 修正tlog作为logger输出时的缓冲区并发问题 <br>
 * 
 * @version 1.6.7.21 [20170303 duanyy] <br>
 * - 增加parameter字段，便于调用者记录个性化参数 <br>
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
	protected String hostPattern = "${server.ip}:${server.port}";
	
	protected String host = null;	
	
	protected void onConfigure(Element _e, Properties p) throws BaseException {
		thread = PropertiesConstants.getInt(p, "thread", 0);
		delimeter = PropertiesConstants.getString(p,"delimeter", delimeter);
		eol = PropertiesConstants.getString(p,"eol", eol);
		hostPattern = PropertiesConstants.getRaw(p,"host",hostPattern);
		app = PropertiesConstants.getString(p, "app", "${server.app}");	
		
		logger = LoggerFactory.getLogger("TLog" + thread);
	}

	protected void onHandle(TLog item,long t) {
		if (host == null){
			host = Settings.get().transform(hostPattern);
		}
		/**
		 * 单条记录的缓存
		 */
		StringBuffer buf = new StringBuffer();

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
		.append(item.parameter()).append(delimeter)
		.append(reason).append(eol);
		
		logger.info(buf.toString());
	}

	protected void onFlush(long t) {
		//no buffer
	}

}
