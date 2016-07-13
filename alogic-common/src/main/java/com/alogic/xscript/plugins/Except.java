package com.alogic.xscript.plugins;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;


/**
 * Except 
 * 
 * <p>
 * Except defines an exception handler in current block.
 * 
 * @author duanyy
 *
 */
public class Except extends AbstractLogiclet{

	public Except(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Element root, Properties p) {
		Properties props = new XmlElementProperties(root,p);
		configure(props);
		
		String id = root.getAttribute("id");
		id = StringUtils.isNotEmpty(id)?id:getXmlTag();

		// 在子节点中找到第一个statement
		NodeList _children = root.getChildNodes();
		for (int i = 0; i < _children.getLength(); i++) {
			Node n = _children.item(i);

			if (n.getNodeType() != Node.ELEMENT_NODE) {
				// 只处理Element节点
				continue;
			}

			Element e = (Element) n;
			String xmlTag = e.getNodeName();
			Logiclet logiclet = createLogiclet(xmlTag, this);

			if (logiclet == null) {
				logger.error("Can not find plugins:" + xmlTag);
			} else {
				logiclet.configure(e, props);
				if (logiclet.isExecutable()) {
					Logiclet parent = parent();
					if (parent != null) {
						parent.registerExceptionHandler(id, logiclet);
					}
				}
				break;
			}
		}		
	}	

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		// it's not an executable logiclet
	}
	
	public boolean isExecutable(){
		return false;
	}	

}
