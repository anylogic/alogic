package com.anysoft.formula;

import com.anysoft.util.DefaultProperties;

public class testVariant {

	public static void main(String[] args) {
		String formula = "23 % 10 + (to_long(id) / 20 - 2*20) + 0.1 * 100";
		
		Parser parser = new Parser();
		Expression expr = parser.parse(formula);
		
		DefaultProperties dataprovider = new DefaultProperties();
		dataprovider.SetValue("id", "200");
			
		ExprValue value = expr.getValue(dataprovider);
		System.out.println(value);
	}

}
