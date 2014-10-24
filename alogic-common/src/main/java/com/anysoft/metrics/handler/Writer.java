package com.anysoft.metrics.handler;

import com.anysoft.metrics.core.Fragment;
import com.anysoft.metrics.core.MetricsHandler;
import com.anysoft.stream.AbstractHandler;

abstract public class Writer extends AbstractHandler<Fragment> implements MetricsHandler{

	
	public void metricsIncr(Fragment fragment) {
		handle(fragment,System.currentTimeMillis());
	}

	
	protected void onHandle(Fragment _data, long timestamp) {
		write(_data,timestamp);
	}

	
	protected void onFlush(long timestamp) {

	}

	abstract protected void write(Fragment data,long t);
}
