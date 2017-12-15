package com.anysoft.selector.impl;

import org.apache.commons.lang3.StringUtils;
import com.anysoft.formula.DataProvider;
import com.anysoft.selector.Selector;
import com.anysoft.util.DataProviderProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * Template
 * @author yyduan
 * @since 1.6.6.13
 * 
 * @version 1.6.11.1 [20171215 duanyy] <br>
 * - 增加final属性 <br>
 */
public class Template extends Selector {	
	protected String template = "";	
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		template = PropertiesConstants.getString(p, "selector-template", template,true);
	}
	
	@Override
	public String onSelect(DataProvider provider) {
		DataProviderProperties p = new DataProviderProperties(provider);
		String value = p.transform(template);		
		return StringUtils.isEmpty(value)?getDefaultValue():value;
	}


}
