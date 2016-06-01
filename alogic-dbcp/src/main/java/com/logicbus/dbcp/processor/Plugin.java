package com.logicbus.dbcp.processor;

import com.anysoft.formula.DataProvider;
import com.anysoft.formula.ExprValue;
import com.anysoft.formula.Expression;
import com.anysoft.formula.FormulaException;
import com.anysoft.formula.Function;
import com.anysoft.util.KeyGen;

/**
 * 插件
 * 
 * @author duanyy
 * 
 * @version 1.6.5.8 [20160601 duanyy] <br>
 * - 考虑到其它数据库的需求，支持自定义绑定变量名(1.6.5.8)
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
			if (getArgumentCount() > 2){
				throw new FormulaException("bind function only supports less than 2 arguments.");
			}	
		}

		@Override
		public ExprValue getValue(DataProvider provider) throws FormulaException {
			int arguCnt = getArgumentCount();
			if (arguCnt > 2 || arguCnt <= 0) {
				throw new FormulaException(
						"bind function need 1 argument.we have "
								+ arguCnt);
			}

			ExprValue value = getArgument(0).getValue(provider);

			String varName = value.toString();
			if (varContext == null) {
				varContext = provider.getContext(varName);
			}

			String object = provider.getValue(varName, varContext, "");
			bind(object);
			
			if (arguCnt <= 1){
				return new ExprValue("?");
			}else{
				return getArgument(1).getValue(provider);
			}
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
			if (getArgumentCount() > 2){
				throw new FormulaException("bind_raw function only supports less than 2 arguments.");
			}	
		}

		@Override
		public ExprValue getValue(DataProvider provider) throws FormulaException {
			int arguCnt = getArgumentCount();
			if (arguCnt > 2 || arguCnt <= 0) {
				throw new FormulaException(
						"bind function need 1 argument.we have "
								+ arguCnt);
			}

			ExprValue value = getArgument(0).getValue(provider);

			String varName = value.toString();
			if (varContext == null) {
				varContext = provider.getContext(varName);
			}

			String object = provider.getRawValue(varName, varContext, "");
			bind(object);
			if (arguCnt <= 1){
				return new ExprValue("?");
			}else{
				return getArgument(1).getValue(provider);
			}
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
			
			ExprValue bindValue = null;
			if (arguCnt <= 0){
				bindValue = new ExprValue("?");
			}else{
				bindValue = getArgument(0).getValue(provider);		
				if (arguCnt <= 1){
					bind(KeyGen.uuid());
				}else{
					if (arguCnt <= 2){
						int length = getArgument(1).getValue(provider).getInt();
						bind(KeyGen.uuid(length, 0));
					}else{
						int length = getArgument(1).getValue(provider).getInt();
						int redix = getArgument(2).getValue(provider).getInt();
						bind(KeyGen.uuid(length, redix));
					}					
				}
			}
			
			return bindValue;
		}
	}	
}
