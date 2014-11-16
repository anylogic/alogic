package com.anysoft.util;

import java.util.Map;

import org.w3c.dom.Element;


/**
 * interface <code>Reportable</code> 
 * 
 * @author duanyy
 * @since 1.3.0
 * 
 */
public interface Reportable {
	/**
	 * 报告输出到XML
	 * @param xml XML节点
	 */
	public void report(Element xml);
	
	/**
	 * 报告输出到JSON
	 * @param json JSON节点
	 */
	public void report(Map<String,Object> json);
}
