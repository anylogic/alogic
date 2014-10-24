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
	 * @param xml
	 * @return 
	 */
	public void report(Element xml);
	
	/**
	 * 报告输出到JSON
	 * @param json
	 * @return 
	 */
	public void report(Map<String,Object> json);
}
