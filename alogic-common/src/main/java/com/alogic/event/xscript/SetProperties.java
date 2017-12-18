package com.alogic.event.xscript;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.event.Event;
import com.alogic.event.EventBuilder;
import com.alogic.event.EventBus;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.selector.Selector;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 设置多个属性
 * 
 * @author yyduan
 * @since 1.6.11.2
 * 
 */
public class SetProperties extends EventBuilder {

	/**
	 * 选择器列表
	 */
	protected List<Selector> properties = new ArrayList<Selector>();
	
	public SetProperties(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
		
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
				logger.error(String.format("Can not create selector with %s",XmlTools.node2String(elem)));
			}
		}
	}
	
	@Override
	protected void onExecute(Event e, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		for (Selector p:properties){
			String k = p.getId();
			String v = p.select(ctx);
			EventBus.setEventProperty(e,k, v, !p.isFinal());
		}
	}

}
