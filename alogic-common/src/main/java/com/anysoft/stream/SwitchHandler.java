package com.anysoft.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.selector.Selector;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlTools;

/**
 * Switch Handler
 * @author yyduan
 *
 * @param <data>
 * 
 * @since 1.6.6.13
 */
public abstract class SwitchHandler <data extends Flowable> extends AbstractHandler<data> {

	/**
	 * 后续的handler
	 */
	protected Map<String,Handler<data>> cases = new HashMap<String,Handler<data>>();
	
	/**
	 * 选择器
	 */
	protected Selector selector = null;
	
	protected Handler<data> defaultHandler = null;
	
	@Override
	protected void onHandle(data d,long t){
		if (selector != null){
			String value = selector.select(d);
			Handler<data> found = cases.get(value);
			if (found != null){
				found.handle(d, t);
			}else{
				if (defaultHandler != null){
					defaultHandler.handle(d, t);
				}
			}
		}
	}
	
	@Override
	protected void onConfigure(Element e, Properties p) {
		NodeList nodeList = XmlTools.getNodeListByPath(e, getHandlerType());
		
		Factory<Handler<data>> factory = new Factory<Handler<data>>();
		
		for (int i = 0 ;i < nodeList.getLength() ; i++){
			Node n = nodeList.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element elem = (Element)n;
			String caseValue = elem.getAttribute("case");
			if (StringUtils.isNotEmpty(caseValue)){
				try{
					Handler<data> newHandler = factory.newInstance(elem, p);
					if (newHandler != null){
						cases.put(caseValue, newHandler);
					}
				}catch (Exception ex){
					LOG.error(String.format("Can not create handler with %s",XmlTools.node2String(elem)));
				}
			}
		}
		
		defaultHandler = cases.get("default");
		
		Element select = XmlTools.getFirstElementByPath(e, "condition");
		if (select != null){
			selector = Selector.newInstance(select, p);
		}else{
			LOG.error("A eval node is needed");
		}
	}
	
	@Override
	protected void onFlush(long timestamp) {
		Iterator<Handler<data>> iter = cases.values().iterator();
		
		while (iter.hasNext()){
			Handler<data> handler = iter.next();
			
			handler.flush(timestamp);
		}
	}	
	
	@Override
	public void close() {
		super.close();
		Iterator<Handler<data>> iter = cases.values().iterator();
		
		while (iter.hasNext()){
			Handler<data> handler = iter.next();
			IOTools.close(handler);
		}
	}
	
	public void report(Element root){
		super.report(root);
		
		Document doc = root.getOwnerDocument();
		Iterator<Handler<data>> iter = cases.values().iterator();
		
		while (iter.hasNext()){
			Handler<data> handler = iter.next();
			Element newHandler = doc.createElement(getHandlerType());
			handler.report(newHandler);
			root.appendChild(newHandler);
		}
	}
	
	
	public void report(Map<String, Object> json){
		super.report(json);
		Iterator<Handler<data>> iter = cases.values().iterator();
		List<Object> list = new ArrayList<Object>();
		while (iter.hasNext()){
			Handler<data> handler = iter.next();
			Map<String, Object> map = new HashMap<String, Object>();
			handler.report(map);
			list.add(map);
		}		
		json.put(getHandlerType(), list);
	}		
}