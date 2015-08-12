package com.anysoft.formula;

public class testSimple {

	public static void main(String[] args) {
		Parser parser = new Parser();
		
		String formula = "23 % 10 + (200 / 20 - 2*20) + 0.1 * 100";
		
		Expression expr = parser.parse(formula);
		
		ExprValue value = expr.getValue(null);
		System.out.println(value);
	}

}
