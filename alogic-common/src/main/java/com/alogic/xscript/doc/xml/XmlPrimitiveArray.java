package com.alogic.xscript.doc.xml;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.xscript.doc.XsPrimitive;
import com.alogic.xscript.doc.XsPrimitiveArray;

/**
 * 基于xml的XsPrimitiveArray
 * 
 * @author yyduan
 * @since 1.6.8.14
 */
public class XmlPrimitiveArray implements XsPrimitiveArray {
	/**
	 * 父节点
	 */
	protected Element parent = null;
	
	protected String xmlTag;
	
	public XmlPrimitiveArray(String tag,Element parent){
		this.xmlTag = tag;
		this.parent = parent;
	}
	
	@Override
	public int getElementCount() {
		NodeList children = parent.getElementsByTagName(xmlTag);
		return children.getLength();
	}

	@Override
	public XsPrimitive get(int index) {
		NodeList children = parent.getElementsByTagName(xmlTag);
		Node n = children.item(index);
		if (n.getNodeType() != Node.ELEMENT_NODE){
			return null;
		}
		return new XmlPrimitive((Element)n);
	}

	@Override
	public void add(Number value) {
		add(value.toString());
	}

	@Override
	public void add(String value) {
		if (StringUtils.isNotEmpty(value)){
			Document doc = parent.getOwnerDocument();
			Element newChild = doc.createElement(xmlTag);
			newChild.appendChild(doc.createTextNode(value));
			parent.appendChild(newChild);
		}
	}

	@Override
	public void add(Boolean value) {
		add(BooleanUtils.toBoolean(value));
	}

}
