package com.logicbus.backend.bizlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.logicbus.models.servant.ServiceDescription.LogType;

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
 * - Handler:handle和flush方法增加timestamp参数，以便时间同步 <br>
 * 
 * @version 1.6.4.11 [20151116 duanyy] <br>
 * - 日志类型为none的服务日志也将输出到bizlog，在此过滤掉为none的输出
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
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
	
	protected String eol = "$$";
	
	/**
	 * 计费标志
	 */
	protected boolean isBilling = true;
	
	/**
	 * 单条记录的缓存
	 */
	protected StringBuffer buf = new StringBuffer();
	
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
		
		buf.append(eol);
		logger.info(buf.toString());
	}
	
	protected void onFlush(long t) {
		//no buffer
	}
}
