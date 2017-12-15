package com.anysoft.selector.impl;

import com.anysoft.formula.DataProvider;
import com.anysoft.formula.DefaultFunctionHelper;
import com.anysoft.formula.Expression;
import com.anysoft.formula.Parser;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.selector.Selector;

/**
 * 公式选择器
 * 
 * @author duanyy
 * @since 1.5.2
 * @version 1.6.11.1 [20171215 duanyy] <br>
 * - 增加final属性 <br>
 */
public class Formula extends Selector {

	protected String formula = "0";
	protected Expression expr = null;	
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		formula = PropertiesConstants.getString(p, "formula", formula,true);
		Parser parser = new Parser(new DefaultFunctionHelper(null));
		expr = parser.parse(formula);
	}

	@Override
	public String onSelect(DataProvider _dataProvider) {
		try{
			return expr.getValue(_dataProvider).toString();
		}catch (Exception ex){
			return getDefaultValue();
		}
	}

}
