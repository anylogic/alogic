package com.alogic.tlog.handler;

import org.w3c.dom.Element;

import com.alogic.tlog.TLog;
import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.Properties;

/**
 * 调试模式
 * 
 * @author yyduan
 * @since 1.6.7.3
 */
public class Debug extends AbstractHandler<TLog> {

	@Override
	protected void onHandle(TLog data, long timestamp) {
		LOG.info(data.toString());
	}

	@Override
	protected void onFlush(long timestamp) {

	}

	@Override
	protected void onConfigure(Element e, Properties p) {

	}

}
