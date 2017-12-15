package com.anysoft.selector.impl;

import com.anysoft.formula.DataProvider;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.selector.Selector;

/**
 * 常量选择器
 * 
 * @author duanyy
 * @since 1.5.2
 * @version 1.6.11.1 [20171215 duanyy] <br>
 * - 增加final属性 <br>
 */
public class Constants extends Selector {

	@Override
	public void configure(Properties p) {
		super.configure(p);
		value = PropertiesConstants.getString(p, "selector-value", value,true);
	}

	@Override
	public String onSelect(DataProvider _dataProvider) {
		return value;
	}

	protected String value = "0";
}
