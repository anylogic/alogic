package com.alogic.event.handler;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.event.Event;
import com.anysoft.selector.Selector;
import com.anysoft.stream.Handler;
import com.anysoft.stream.SlideHandler;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;

/**
 * 为事件增加属性
 * 
 * @author yyduan
 *
 */
public class AddProperties extends SlideHandler<Event>{
	/**
	 * 待增加的属性
	 */
	protected List<Selector> properties = new ArrayList<Selector>();

	/**
	 * 是否覆盖
	 */
	protected boolean overwrite;
	
	@Override
	protected void onHandle(Event e, long timestamp) {
		for (Selector p:properties){
			String k = p.getId();
			String v = p.select(e);
			e.setProperty(k, v, overwrite);
		}
		
		Handler<Event> handler = getSlidingHandler();
		if (handler != null){
			handler.handle(e, timestamp);
		}
	}

	@Override
	protected void onConfigure(Element e, Properties p) {
		super.onConfigure(e, p);
		
		overwrite = PropertiesConstants.getBoolean(p, "overwrite", true);
		
		NodeList nodeList = XmlTools.getNodeListByPath(e, "properties/property");
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
