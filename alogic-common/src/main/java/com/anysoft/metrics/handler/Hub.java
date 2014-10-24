package com.anysoft.metrics.handler;

import com.anysoft.metrics.core.Fragment;
import com.anysoft.metrics.core.MetricsHandler;
import com.anysoft.stream.HubHandler;

/**
 * Hub处理器
 * @author duanyy
 *
 */
public class Hub extends HubHandler<Fragment> implements MetricsHandler{
	public String getHandlerType(){
		return "handler";
	}

	
	public void metricsIncr(Fragment fragment) {
		handle(fragment,System.currentTimeMillis());
	}
}	