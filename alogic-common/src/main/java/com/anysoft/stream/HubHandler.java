package com.anysoft.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlTools;

/**
 * 集线器
 * 
 * @author duanyy
 *
 * @param <data>
 * 
 * @since 1.4.0
 * 
 * @version 1.4.4 [20140917 duanyy]
 * - Handler:handle和flush方法增加timestamp参数，以便进行时间同步
 */
public class HubHandler<data extends Flowable> extends AbstractHandler<data> {
	protected static Logger logger = LogManager.getLogger(HubHandler.class);
	/**
	 * handlers
	 */
	protected List<Handler<data>> handlers = new ArrayList<Handler<data>>();

	
	protected void onHandle(data _data,long timestamp) {
		for (Handler<data> h:handlers){
			if (h != null){
				h.handle(_data,timestamp);
			}
		}
	}

	
	protected void onFlush(long timestamp) {
		for (Handler<data> h:handlers){
			if (h != null){
				h.flush(timestamp);
			}
		}
	}
	public void close() throws Exception{
		super.close();
		for (Handler<data> h:handlers){
			if (h != null){
				IOTools.close(h);
			}
		}
		handlers.clear();
	}
	
	
	public void report(Element root){
		super.report(root);
		
		if (handlers != null){
			Document doc = root.getOwnerDocument();
			
			for (Handler<data> _handler:handlers){
				if (_handler != null){
					Element newHandler = doc.createElement(getHandlerType());
					_handler.report(newHandler);
					root.appendChild(newHandler);
				}
			}
		}
	}
	
	
	public void report(Map<String, Object> json){
		super.report(json);
		
		if (handlers != null){
			List<Object> array = new ArrayList<Object>(handlers.size());
			
			for (Handler<data> _handler:handlers){
				if (_handler != null){
					Map<String,Object> map = new HashMap<String,Object>();
					_handler.report(map);
					array.add(map);
				}
			}
			
			json.put(getHandlerType(), array);
		}
	}
	
	
	protected void onConfigure(Element e, Properties p) {
		NodeList children = XmlTools.getNodeListByPath(e, getHandlerType());
		
		Factory<Handler<data>> factory = new Factory<Handler<data>>();
		
		for (int i = 0 ; i < children.getLength() ; i ++){
			Node n = children.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			try {
				Handler<data> newHandler = factory.newInstance((Element)n,p);
				if (newHandler != null){
					handlers.add(newHandler);
				}
			}catch (Exception ex){
				logger.error("Can not create handler instance",ex);
			}
		}
	}

}
