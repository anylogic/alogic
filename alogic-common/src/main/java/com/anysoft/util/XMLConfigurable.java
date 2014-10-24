package com.anysoft.util;

import org.w3c.dom.Element;

/**
 * Can read config from a XML document.
 * @author duanyy
 *
 */
public interface XMLConfigurable {
	/**
	 * to read config from xml.
	 * @param _e xml document
	 * @param _properties variables
	 */
	public void configure(Element _e,Properties _properties) throws BaseException;
}
