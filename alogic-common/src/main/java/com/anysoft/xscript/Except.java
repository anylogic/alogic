package com.anysoft.xscript;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;

/**
 * except语句
 * 
 * @author duanyy
 * @since 1.6.3.22
 */
public class Except extends AbstractStatement {
	public Except(String _tag, Statement _parent) {
		super(_tag, _parent);
	}

	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e, _properties);

		String id = _e.getAttribute("id");
		id = isNotNull(id)?id:getXmlTag();

		// 在子节点中找到第一个statement
		NodeList _children = _e.getChildNodes();
		for (int i = 0; i < _children.getLength(); i++) {
			Node n = _children.item(i);

			if (n.getNodeType() != Node.ELEMENT_NODE) {
				// 只处理Element节点
				continue;
			}

			Element e = (Element) n;
			String xmlTag = e.getNodeName();
			Statement statement = createStatement(xmlTag, this);

			if (statement == null) {
				logger.warn("Can not find plugin:" + xmlTag + ",Ignored.");
			} else {
				statement.configure(e, p);
				if (statement.isExecutable()) {
					Statement _parent = parent();
					if (_parent == null) {
						logger.warn("Parent statement is null,ignored");
					} else {
						_parent.registerExceptionHandler(id, statement);
					}
				}
				break;
			}
		}
	}
	
	public boolean isExecutable(){
		return false;
	}

	public int onExecute(Properties p,ExecuteWatcher watcher) {
		return 0;
	}
	
	protected boolean isNotNull(String value){
		return value != null && value.length() > 0;
	}

}
