package com.anysoft.selector;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 字段列表
 * 
 * @author duanyy
 * 
 * @since 1.5.2
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */

public class FieldList implements XMLConfigurable {
	protected Logger logger = LoggerFactory.getLogger(FieldList.class);

	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e, _properties);

		NodeList children = _e.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element e = (Element) n;
			try {
				Selector newSelector = Selector.newInstance(e, p);
				fields.add(newSelector);
			} catch (Exception ex) {
				logger.error("Can not create selector",ex);
			}
		}
		
	}
	
	public Selector[] getFields() {
		return fields.toArray(new Selector[0]);
	}

	protected Vector<Selector> fields = new Vector<Selector>();	
}
