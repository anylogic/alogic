package com.anysoft.util;

import org.w3c.dom.Element;

/**
 * XML序列化接口
 * @author hmyyduan
 *
 */
public interface XmlSerializer {
	/**
	 * 写出到XML节点
	 * @param e element
	 */
	public void toXML(Element e);
	/**
	 * 从XML节点读入
	 * @param e element
	 */
	public void fromXML(Element e);
}
