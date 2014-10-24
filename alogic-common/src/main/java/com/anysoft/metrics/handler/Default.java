package com.anysoft.metrics.handler;

import org.w3c.dom.Element;

import com.anysoft.metrics.core.Fragment;
import com.anysoft.metrics.core.MetricsHandler;
import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.Properties;

/**
 * 缺省的处理器
 * @author duanyy
 *
 */
public class Default extends AbstractHandler<Fragment> implements MetricsHandler{

	
	protected void onHandle(Fragment _data,long t) {
	}

	
	protected void onFlush(long t) {
	}

	
	protected void onConfigure(Element e, Properties p) {
	}

	
	public void metricsIncr(Fragment fragment) {
		handle(fragment,System.currentTimeMillis());
	}
	
}