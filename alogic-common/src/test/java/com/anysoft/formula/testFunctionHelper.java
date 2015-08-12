package com.anysoft.formula;

import com.anysoft.util.DefaultProperties;

public class testFunctionHelper {

	public static void main(String[] args) {
		String formula = "age() % 10 + (to_long(id) / 20 - 2*20) + 0.1 * 100";
		
		final Function age = new Function("age"){
			public void checkArgument(Expression arg) throws FormulaException {
				if (getArgumentCount() > 0){
					throw new FormulaException("age function does not support any arguments.");
				}
			}

			public ExprValue getValue(DataProvider provider)
					throws FormulaException {
				return new ExprValue(23);
			}
		};
		
		FunctionHelper myFunctionHelper = new FunctionHelper(){
			public Expression customize(String funcName) {
				if (funcName.equals("age")){
					return age;
				}
				return null;
			}			
		};
		
		Parser parser = new Parser(new DefaultFunctionHelper(myFunctionHelper));
		Expression expr = parser.parse(formula);
		
		DefaultProperties dataprovider = new DefaultProperties();
		dataprovider.SetValue("id", "200");
			
		ExprValue value = expr.getValue(dataprovider);
		System.out.println(value);
	}

}
