package com.alogic.metrics.stream.handler;

import org.w3c.dom.Element;

import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.Properties;
import com.alogic.metrics.Fragment;

/**
 * Quiet
 * 
 * 安静模式的处理器，不做任何处理
 * 
 * @author yyduan
 *
 * @since 1.6.6.13
 *
 */
public class Quiet extends AbstractHandler<Fragment>{

	@Override
	protected void onHandle(Fragment _data, long timestamp) {
		// nothing to do
	}

	@Override
	protected void onFlush(long timestamp) {
		// nothing to do
	}

	@Override
	protected void onConfigure(Element e, Properties p) {
		// nothing to do
	}

}
