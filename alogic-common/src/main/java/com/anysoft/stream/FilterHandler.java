package com.anysoft.stream;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlTools;

/**
 * 过滤器
 * 
 * @author duanyy
 *
 * @param <data>
 */
public abstract class FilterHandler <data extends Flowable> extends AbstractHandler<data> {
	protected static Logger logger = LogManager.getLogger(FilterHandler.class);
	protected Handler<data> handler = null;
	
	@Override
	protected void onHandle(data _data, long timestamp) {
		if (handler != null && accept(_data)){
			handler.handle(_data, timestamp);
		}
	}

	protected abstract boolean accept(data d);

	@Override
	protected void onFlush(long timestamp) {
		if (handler != null){
			handler.flush(timestamp);
		}
	}
	
	public void close() throws Exception{
		super.close();
		IOTools.close(handler);
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

	@Override
	protected void onConfigure(Element e, Properties p) {
		Element child = XmlTools.getFirstElementByPath(e, getHandlerType());
		if (child != null){
			Factory<Handler<data>> factory = new Factory<Handler<data>>();
			try {
				handler = factory.newInstance(child,p);
			}catch (Exception ex){
				logger.error("Can not create handler instance",ex);
			}			
		}
	}
}
