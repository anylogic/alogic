package com.alogic.event.handler;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.event.Event;
import com.alogic.event.EventProperties;
import com.anysoft.formula.DataProvider;
import com.anysoft.selector.Selector;
import com.anysoft.stream.Handler;
import com.anysoft.stream.SlideHandler;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;

/**
 * 为事件增加属性
 * 
 * @author yyduan
 * 
 * @version 1.6.11.1 [20171215 duanyy] <br>
 * - 可以按照属性来设置overwrite行为 <br>
 * 
 */
public class AddProperties extends SlideHandler<Event>{
	/**
	 * 待增加的属性
	 */
	protected List<Selector> properties = new ArrayList<Selector>();
	
	@Override
	protected void onHandle(Event e, long timestamp) {
		DataProvider df = new EventProperties(e,Settings.get());
		for (Selector p:properties){
			String k = p.getId();
			String v = p.select(df);
			e.setProperty(k, v, !p.isFinal());
		}
		
		Handler<Event> handler = getSlidingHandler();
		if (handler != null){
			handler.handle(e, timestamp);
		}
	}

	@Override
	protected void onConfigure(Element e, Properties p) {
		super.onConfigure(e, p);
		
		NodeList nodeList = XmlTools.getNodeListByPath(e, "property");
		for (int i = 0 ;i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element elem = (Element)n;
			
			try{
				Selector selector = Selector.newInstance(elem, p);
				if (selector != null){
					properties.add(selector);
				}
			}catch (Exception ex){
				LOG.error(String.format("Can not create selector with %s",XmlTools.node2String(elem)));
			}
		}
		
	}
}
