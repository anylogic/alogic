package com.anysoft.webloader;

import javax.servlet.ServletConfig;

import org.apache.commons.lang3.StringUtils;

import com.anysoft.util.Properties;
import com.anysoft.util.Settings;

/**
 * A Properties wrapper for ServletConfig.
 * 
 * @author duanyy
 * @version 1.6.8.13 [duanyy 20170427] <br>
 * - 指定Settings为父节点 <br>
 */
public class ServletConfigProperties extends Properties {

	protected ServletConfig servletConfig = null;
	
	public ServletConfigProperties(ServletConfig sc){
		super("ServletConfig",Settings.get());
		servletConfig = sc;
	}
	
	protected void _SetValue(String _name, String _value) {
		//do nothing
	}

	
	protected String _GetValue(String _name) {
		if (servletConfig == null)
			return "";
		String value = servletConfig.getInitParameter(_name);
		return StringUtils.isEmpty(value)?servletConfig.getServletContext().getInitParameter(_name) : value;
	}

	
	public void Clear() {
		// do nothing

	}

}
