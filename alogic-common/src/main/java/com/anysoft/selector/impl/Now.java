package com.anysoft.selector.impl;

import org.w3c.dom.Element;

import com.anysoft.formula.DataProvider;
import com.anysoft.util.Properties;
import com.anysoft.selector.Selector;

/**
 * 当前时间
 * 
 * @author duanyy
 * 
 * @since 1.5.2
 *
 */
public class Now extends Selector {

	
	public void onConfigure(Element _e, Properties _p) {
	}

	
	public String onSelect(DataProvider _dataProvider) {
		return String.valueOf(System.currentTimeMillis());
	}

}
