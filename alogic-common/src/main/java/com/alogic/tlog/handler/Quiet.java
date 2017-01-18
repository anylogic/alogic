package com.alogic.tlog.handler;

import org.w3c.dom.Element;

import com.alogic.tlog.TLog;
import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.Properties;

/**
 * 安静模式
 * 
 * @author yyduan
 * @since 1.6.7.3
 */
public class Quiet extends AbstractHandler<TLog> {

	@Override
	protected void onHandle(TLog _data, long timestamp) {

	}

	@Override
	protected void onFlush(long timestamp) {

	}

	@Override
	protected void onConfigure(Element e, Properties p) {

	}

}
