package com.anysoft.stream;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlTools;

/**
 * Slide
 * 
 * @author yyduan
 *
 * @param <data>
 * 
 * @since 1.6.6.13
 */
public abstract class SlideHandler<data extends Flowable> extends AbstractHandler<data> {

	/**
	 * 后续的handler
	 */
	protected Handler<data> handler = null;

	@Override
	protected void onConfigure(Element e, Properties p) {
		Element found = XmlTools.getFirstElementByPath(e, getHandlerType());
		if (found != null){
			Factory<Handler<data>> factory = new Factory<Handler<data>>();
			handler = factory.newInstance(found, p);
		}
	}
	
	/**
	 * 获取后续的handler
	 * @return handler
	 */
	public Handler<data> getSlidingHandler(){
		return handler;
	}
	
	@Override
	protected void onFlush(long timestamp) {
		Handler<data> handler = getSlidingHandler();
		if (handler != null){
			handler.flush(timestamp);
		}
	}	
	
	@Override
	public void close() throws Exception{
		super.close();
		if (handler != null){
			handler.close();
		}
	}
	
	public void report(Element root){
		super.report(root);
		
		if (handler != null) {
			Document doc = root.getOwnerDocument();

			Element newHandler = doc.createElement(getHandlerType());
			handler.report(newHandler);
			root.appendChild(newHandler);
		}
	}
	
	
	public void report(Map<String, Object> json){
		super.report(json);
		
		if (handler != null) {
			Map<String, Object> map = new HashMap<String, Object>();
			handler.report(map);
			json.put(getHandlerType(), map);
		}
	}		
}
