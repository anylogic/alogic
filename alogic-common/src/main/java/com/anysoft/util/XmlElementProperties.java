package com.anysoft.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XMLElement变量集
 * <p>将Element的属性集合封装成变量集。</p>
 * @author duanyy
 *
 */
public class XmlElementProperties extends Properties {
	/**
	 * 对应的Element
	 */
	private Element m_element = null;
	/**
	 * 扩展的变量集
	 */
	private DefaultProperties extProps = null;
	
	/**
	 * 构造函数
	 * @param element 对应的Element
	 * @param parent 父节点
	 */
	public XmlElementProperties(Element element,Properties parent){
		super("Default",parent);
		m_element = element;
		extProps = new DefaultProperties();
		
		if (element != null){
			Element ext = XmlTools.getFirstElementByPath(element, "properties");
			if (ext != null){
				NodeList nodeList = ext.getChildNodes();
				for (int i= 0 ; i < nodeList.getLength() ; i ++){
					Node node = nodeList.item(i);
					if (node.getNodeType() != Node.ELEMENT_NODE){
						continue;
					}
					if (!node.getNodeName().equals("parameter")){
						continue;
					}
					Element _e = (Element)node;
					String id = _e.getAttribute("id");
					String value = _e.getAttribute("value");
					if (id.length() <= 0){
						continue;
					}
					extProps.SetValue(id, value);
				}
			}
		}
	}
	
	/**
	 * 清除,但是XMLElement的属性是只读的
	 */
	public void Clear() {
		//不实现clear方法		
	}

	/**
	 * 获取变量
	 * @param _name
	 */
	protected String _GetValue(String _name) {
		if (m_element != null)
		{
			String value = m_element.getAttribute(_name);
			if (value.length() <= 0){
				return extProps.GetValue(_name, "", false, true);
			}
			return value;
		}
		return "";
	}

	/**
	 * 设置变量
	 * @param _name 变量名
	 * @param _value 变量值
	 */
	protected void _SetValue(String _name, String _value) {
		if (m_element != null){
			m_element.setAttribute(_name, _value);
		}
	}

}
