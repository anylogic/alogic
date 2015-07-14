package com.logicbus.dbcp.processor;

import com.anysoft.formula.DataProvider;
import com.anysoft.formula.ExprValue;
import com.anysoft.formula.Expression;
import com.anysoft.formula.FormulaException;

/**
 * value插件
 * 
 * @author duanyy
 * @since 1.6.3.30
 */
public class Value extends Plugin {
	protected Object varContext = null;
	
	public Value(String name, BindedListener listener) {
		super(name, listener);
	}

	@Override
	public void checkArgument(Expression arg) throws FormulaException {
		if (getArgumentCount() != 1){
			throw new FormulaException("value function only supports 1 argument.");
		}	
	}

	@Override
	public ExprValue getValue(DataProvider provider) throws FormulaException {
		if (getArgumentCount() != 1) {
			throw new FormulaException(
					"value function need 1 argument.we have "
							+ getArgumentCount());
		}

		ExprValue value = getArgument(0).getValue(provider);

		String varName = value.toString();
		if (varContext == null) {
			varContext = provider.getContext(varName);
		}

		String object = provider.getValue(varName, varContext, null);
		bind(object);
		return new ExprValue("?");
	}
}
