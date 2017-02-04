package com.logicbus.backend.bizlog.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.logicbus.backend.bizlog.BizLogItem;
import com.logicbus.backend.bizlog.BizLogger;
import com.logicbus.models.servant.ServiceDescription.LogType;

/**
 * log4j文件写出
 * @author yyduan
 *
 * @since 1.6.7.12
 */
public class Log4j extends AbstractHandler<BizLogItem> implements BizLogger {

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
	
	protected String eol = "$$";
	
	/**
	 * 计费标志
	 */
	protected boolean isBilling = true;
		
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
	protected String hostPattern = "${server.host}:${server.port}";
	
	protected String host = null;
	
	
	protected void onConfigure(Element _e, Properties p) throws BaseException {
		thread = PropertiesConstants.getInt(p, "thread", 0);
		delimeter = PropertiesConstants.getString(p,"delimeter", delimeter);
		eol = PropertiesConstants.getString(p,"eol", eol);
		isBilling = PropertiesConstants.getBoolean(p,"billing", isBilling);
		app = PropertiesConstants.getString(p, "app", "${server.app}");
		hostPattern = PropertiesConstants.getRaw(p,"host",hostPattern);
		
		logger = LoggerFactory.getLogger("Bizlog" + thread);
	}

	
	protected void onHandle(BizLogItem item,long t) {
		if (item.logType == LogType.none){
			return;
		}
	
		if (host == null){
			host = Settings.get().transform(hostPattern);
		}
		
		/**
		 * 单条记录的缓存
		 */
		StringBuffer buf = new StringBuffer();
		
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
		
		buf.append(eol);
		logger.info(buf.toString());
	}
	
	protected void onFlush(long t) {
		//no buffer
	}
}
