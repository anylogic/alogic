package com.anysoft.selector.impl;

import org.w3c.dom.Element;

import com.anysoft.formula.DataProvider;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;
import com.anysoft.selector.Selector;

/**
 * 日期格式化器
 * 
 * @author duanyy
 * 
 * @since 1.5.2
 * @version 1.6.11.1 [20171215 duanyy] <br>
 * - 增加final属性 <br>
 */
public class DateFormatter extends Selector {

	@Override
	public void onConfigure(Element _e, Properties _p) {
		pattern = PropertiesConstants.getString(_p,"pattern",pattern,true);
		
		Element _selector = XmlTools.getFirstElementByPath(_e, "selector");
		if (_selector == null){
			selector = Selector.newInstance(_e, _p, SingleField.class.getName());
		}else{
			selector = Selector.newInstance(_selector, _p);
		}
	}

	@Override
	public String onSelect(DataProvider _dataProvider) {
		String value = selector.select(_dataProvider);
		long t = Long.parseLong(value);	
		return String.format(pattern, t);
	}

	protected Selector selector = null;
	
	protected String pattern = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS";
}
