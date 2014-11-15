package com.anysoft.selector.impl;

import org.w3c.dom.Element;

import com.anysoft.formula.DataProvider;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.selector.Selector;


/**
 * 单字段选择器
 * 
 * @author duanyy
 * @since 1.5.2
 * 
 * @version 1.6.0.5 [20141114 duanyy] <br>
 * - 如果没有定义selector-field属性，则取selector-id属性.<br>
 */
public class SingleField extends Selector {

	
	public void onConfigure(Element _e, Properties _p) throws BaseException {
		fieldName = PropertiesConstants.getString(_p, "selector-field", id,true);
	}

	
	public String onSelect(DataProvider _dataProvider) {
		if (context == null){
			context = _dataProvider.getContext(fieldName);
		}
		
		if (context != null){
			return _dataProvider.getValue(fieldName, context, getDefaultValue()).trim();
		}
		
		return getDefaultValue();
	}

	protected Object context = null;
	
	protected String fieldName;	
}
