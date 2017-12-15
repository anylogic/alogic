package com.anysoft.selector.impl;


import com.anysoft.formula.DataProvider;
import com.anysoft.selector.Selector;

/**
 * 当前时间
 * 
 * @author duanyy
 * 
 * @since 1.5.2
 * @version 1.6.11.1 [20171215 duanyy] <br>
 * - 增加final属性 <br>
 */
public class Now extends Selector {

	@Override
	public String onSelect(DataProvider _dataProvider) {
		return String.valueOf(System.currentTimeMillis());
	}

}
