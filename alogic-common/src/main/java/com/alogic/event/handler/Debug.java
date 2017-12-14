package com.alogic.event.handler;

import org.w3c.dom.Element;

import com.alogic.event.Event;
import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.Properties;

/**
 * 用于调试
 * @author yyduan
 *
 */
public class Debug extends AbstractHandler<Event> {

	@Override
	protected void onHandle(Event data, long timestamp) {
		LOG.info(data.toString());
	}

	@Override
	protected void onFlush(long timestamp) {

	}

	@Override
	protected void onConfigure(Element e, Properties p) {

	}

}
