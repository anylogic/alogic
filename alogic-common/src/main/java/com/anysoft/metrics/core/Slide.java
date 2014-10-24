package com.anysoft.metrics.core;

import org.w3c.dom.Element;

import com.anysoft.stream.AbstractHandler;
import com.anysoft.stream.Handler;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlTools;

/**
 * 只有一个后续节点的处理器
 * @author duanyy
 *
 */
abstract public class Slide extends AbstractHandler<Fragment> implements MetricsHandler{
	public void close() throws Exception{
		super.close();
		if (handler != null){
			handler.close();
		}
	}		
	
	public Handler<Fragment> getSlidingHandler(){
		return handler;
	}
	
	
	protected void onConfigure(Element e, Properties p) {
		Element found = XmlTools.getFirstElementByPath(e, getHandlerType());
		if (found != null){
			handler = TheFactory.getInstance(found, p);
		}
	}
	
	public void metricsIncr(Fragment fragment) {
		handle(fragment,System.currentTimeMillis());
	}
	protected Handler<Fragment> handler = null;
}