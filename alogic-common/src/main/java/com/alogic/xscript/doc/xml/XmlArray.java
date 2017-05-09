package com.alogic.xscript.doc.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.xscript.doc.XsArray;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.XmlTools;

/**
 * 基于xml的XsArray
 * 
 * @author yyduan
 * @since 1.6.8.14
 */
public class XmlArray implements XsArray {
	/**
	 * 父节点
	 */
	protected Element parent = null;
	
	protected String xmlTag;
	
	public XmlArray(String tag,Element parent){
		this.xmlTag = tag;
		this.parent = parent;
	}
	@Override
	public XsObject newObject() {
		return new XmlObject(xmlTag,parent.getOwnerDocument().createElement(xmlTag));
	}

	@Override
	public void add(XsObject data) {
		parent.appendChild((Node) data.getContent());
	}

	@Override
	public int getElementCount() {
		NodeList children = XmlTools.getNodeListByPath(this.parent, xmlTag);
		return children.getLength();
	}

	@Override
	public XsObject get(int index) {
		NodeList children = XmlTools.getNodeListByPath(this.parent, xmlTag);
		return new XmlObject(xmlTag,(Element) children.item(index));
	}

}
