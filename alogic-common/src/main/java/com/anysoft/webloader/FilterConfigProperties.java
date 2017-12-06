package com.anysoft.webloader;

import javax.servlet.FilterConfig;
import org.apache.commons.lang3.StringUtils;

import com.anysoft.util.Properties;
import com.anysoft.util.Settings;

/**
 * A Properties wrapper for FilterConfig.
 * 
 * @author duanyy
 * @version 1.6.8.13 [duanyy 20170427] <br>
 * - 指定Settings为父节点 <br>
 */
public class FilterConfigProperties extends Properties {

	protected FilterConfig filterConfig = null;
	
	public FilterConfigProperties(FilterConfig fc){
		super("FilterConfig",Settings.get());
		filterConfig = fc;
	}
	
	protected void _SetValue(String _name, String _value) {
		//do nothing
	}

	
	protected String _GetValue(String _name) {
		if (filterConfig == null)
			return "";
		String value = filterConfig.getInitParameter(_name);
		return StringUtils.isEmpty(value)?filterConfig.getServletContext().getInitParameter(_name) : value;
	}

	
	public void Clear() {
		// do nothing
	}
}