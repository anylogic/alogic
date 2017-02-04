package com.logicbus.backend.bizlog.handler;

import org.w3c.dom.Element;

import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.Properties;
import com.logicbus.backend.bizlog.BizLogItem;
import com.logicbus.backend.bizlog.BizLogger;

/**
 * 调试模式
 * 
 * @author yyduan
 * @since 1.6.7.12
 */
public class Debug extends AbstractHandler<BizLogItem> implements BizLogger{

	@Override
	protected void onHandle(BizLogItem data, long timestamp) {
		LOG.info(data.toString());
	}

	@Override
	protected void onFlush(long timestamp) {

	}

	@Override
	protected void onConfigure(Element e, Properties p) {

	}

}
