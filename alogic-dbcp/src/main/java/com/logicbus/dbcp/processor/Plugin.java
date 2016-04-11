package com.logicbus.dbcp.processor;

import com.anysoft.formula.DataProvider;
import com.anysoft.formula.ExprValue;
import com.anysoft.formula.Expression;
import com.anysoft.formula.FormulaException;
import com.anysoft.formula.Function;
import com.anysoft.util.KeyGen;

/**
 * 插件
 * @author duanyy
 *
 */
abstract public class Plugin extends Function {
	protected BindedListener bindedListener = null;
	
	public Plugin(String name,BindedListener listener){
		super(name);
		bindedListener = listener;
	}
	
	public void bind(Object value){
		if (bindedListener != null){
			bindedListener.bind(value);
		}
	}
	
	/**
	 * NotNull
	 * @author duanyy
	 *
	 */
	public static class NotNull extends Plugin {

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
			if (value == null){
				value = new ExprValue("");
			}
			return value;
		}
	}
	
	/**
	 * bind插件
	 * 
	 * @author duanyy
	 * @since 1.6.3.30
	 */
	public static class Bind extends Plugin {
		protected Object varContext = null;
		
		public Bind(String name, BindedListener listener) {
			super(name, listener);
		}

		@Override
		public void checkArgument(Expression arg) throws FormulaException {
			if (getArgumentCount() > 1){
				throw new FormulaException("bind function only supports 1 argument.");
			}	
		}

		@Override
		public ExprValue getValue(DataProvider provider) throws FormulaException {
			if (getArgumentCount() != 1) {
				throw new FormulaException(
						"bind function need 1 argument.we have "
								+ getArgumentCount());
			}

			ExprValue value = getArgument(0).getValue(provider);

			String varName = value.toString();
			if (varContext == null) {
				varContext = provider.getContext(varName);
			}

			String object = provider.getValue(varName, varContext, "");
			bind(object);
			return new ExprValue("?");
		}
	}
	
	/**
	 * bind插件
	 * 
	 * @author duanyy
	 * @since 1.6.3.30
	 */
	public static class BindRaw extends Plugin {
		protected Object varContext = null;
		
		public BindRaw(String name, BindedListener listener) {
			super(name, listener);
		}

		@Override
		public void checkArgument(Expression arg) throws FormulaException {
			if (getArgumentCount() > 1){
				throw new FormulaException("bind function only supports 1 argument.");
			}	
		}

		@Override
		public ExprValue getValue(DataProvider provider) throws FormulaException {
			if (getArgumentCount() != 1) {
				throw new FormulaException(
						"bind function need 1 argument.we have "
								+ getArgumentCount());
			}

			ExprValue value = getArgument(0).getValue(provider);

			String varName = value.toString();
			if (varContext == null) {
				varContext = provider.getContext(varName);
			}

			String object = provider.getRawValue(varName, varContext, "");
			bind(object);
			return new ExprValue("?");
		}
	}	
	
	/**
	 * uuid
	 * 
	 * @author duanyy
	 * @since 1.6.3.30
	 */
	public static class UUId extends Plugin {
		protected Object varContext = null;
		
		public UUId(String name, BindedListener listener) {
			super(name, listener);
		}

		@Override
		public void checkArgument(Expression arg) throws FormulaException {

		}

		@Override
		public ExprValue getValue(DataProvider provider) throws FormulaException {
			int arguCnt = getArgumentCount();
			if (arguCnt <= 0){
				bind(KeyGen.uuid());
			}else{
				if (arguCnt <= 1){
					int length = getArgument(0).getValue(provider).getInt();
					bind(KeyGen.uuid(length, 0));
				}else{
					int length = getArgument(0).getValue(provider).getInt();
					int redix = getArgument(1).getValue(provider).getInt();
					bind(KeyGen.uuid(length, redix));
				}
			}
			
			return new ExprValue("?");
		}
	}	
}
