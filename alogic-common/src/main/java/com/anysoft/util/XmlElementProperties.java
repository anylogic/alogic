package com.anysoft.util;

import org.w3c.dom.Element;

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
	 * 构造函数
	 * @param element 对应的Element
	 * @param parent 父节点
	 */
	public XmlElementProperties(Element element,Properties parent){
		super("Default",parent);
		m_element = element;
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
			return m_element.getAttribute(_name);
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
