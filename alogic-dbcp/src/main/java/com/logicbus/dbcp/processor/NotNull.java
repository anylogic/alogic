package com.logicbus.dbcp.processor;

import com.anysoft.formula.DataProvider;
import com.anysoft.formula.ExprValue;
import com.anysoft.formula.Expression;
import com.anysoft.formula.FormulaException;

/**
 * NotEmpty
 * @author duanyy
 *
 */
public class NotNull extends Plugin {

	public NotNull(String name, BindedListener listener) {
		super(name, listener);
	}

	@Override
	public void checkArgument(Expression arg) throws FormulaException {
		if (getArgumentCount() > 2){
			throw new FormulaException("not_nvl function only supports 2 arguments.");
		}	
	}

	@Override
	public ExprValue getValue(DataProvider provider) throws FormulaException {
		if (getArgumentCount() != 2){
			throw new FormulaException("not_nvl function need 2 arguments.we have " + getArgumentCount());
		}
		
		ExprValue value = getArgument(0).getValue(provider);
		if (value != null){
			value = getArgument(1).getValue(provider);
		}
		return value;
	}
}
