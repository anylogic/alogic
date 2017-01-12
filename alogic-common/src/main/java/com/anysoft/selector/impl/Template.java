package com.anysoft.selector.impl;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.anysoft.formula.DataProvider;
import com.anysoft.selector.Selector;
import com.anysoft.util.BaseException;
import com.anysoft.util.DataProviderProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * Template
 * @author yyduan
 * @since 1.6.6.13
 */
public class Template extends Selector {	
	protected String template = "";	
	
	public void onConfigure(Element _e, Properties _p) throws BaseException {
		template = PropertiesConstants.getString(_p, "selector-template", template,true);
	}

	@Override
	public String onSelect(DataProvider provider) {
		DataProviderProperties p = new DataProviderProperties(provider);
		String value = p.transform(template);		
		return StringUtils.isEmpty(value)?getDefaultValue():value;
	}


}
