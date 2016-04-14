package com.logicbus.backend.bizlog;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

public class Debug extends AbstractHandler<BizLogItem> implements BizLogger {
	/**
	 * a logger of log4j
	 */
	private static final Logger LOG = LogManager.getLogger(Debug.class);
	
	/**
	 * 单条记录的缓存
	 */
	protected StringBuffer buf = new StringBuffer();	
	
	/**
	 * 输出间隔符
	 */
	protected String delimeter = "%%";	
	
	@Override
	protected void onHandle(BizLogItem item, long timestamp) {
		buf.setLength(0);
		
		buf.append(item.sn).append(delimeter)
		.append(item.startTime).append(delimeter)
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
		LOG.info(buf.toString());
	}

	@Override
	protected void onFlush(long timestamp) {
		// nothing to do
	}

	@Override
	protected void onConfigure(Element e, Properties p) {
		// nothing to do
		delimeter = PropertiesConstants.getString(p,"delimeter", delimeter);		
	}

}
