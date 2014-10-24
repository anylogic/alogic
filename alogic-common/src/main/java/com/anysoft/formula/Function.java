package com.anysoft.formula;

import java.util.Date;
import java.util.Vector;


/**
 * Function
 * @author duanyy
 * @version 1.0.0
 */
abstract public class Function extends Expression{

	/**
	 * constructor
	 * @param _prototype the prototype of the function
	 */
	public Function(String _prototype) {
		super(Operator.OP_Extend);
		prototype = _prototype;
	}

	/**
	 * arguments
	 */
	protected Vector<Expression> args = new Vector<Expression>();
	
	/**
	 * prototype
	 */
	protected String prototype = "Function";
	/**
	 * to get argument list
	 * @return argument list
	 */
	public Expression [] arguments(){
		return args.toArray(new Expression[0]);
	}
	
	/**
	 * to get count of argument
	 * @return count
	 */
	public int getArgumentCount(){
		return args.size();
	}
	
	/**
	 * to get argument by index
	 * @param index index of argument
	 * @return argument
	 */
	public Expression getArgument(int index){
		return args.elementAt(index);
	}
	
	
	public String getOperatorPrototype() {
		return prototype;
	}	
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(getOperatorPrototype());
		buffer.append("(");
		
		for (int i = 0 ; i < getArgumentCount() ; i ++){
			if (i != 0){
				buffer.append(",");
			}
			buffer.append(getArgument(i).toString());
		}
		
		buffer.append(")");
		return buffer.toString();
	}
	/**
	 * to add a argument to argument list
	 * @param arg the argument added
	 * @return this
	 */
	public Function addArgument(Expression arg){
		if (arg == null){
			throw new FormulaException("argument can not be null.");
		}
		checkArgument(arg);
		args.add(arg);
		return this;
	}
	
	/**
	 * to check argument before added
	 * @param arg the argument to be checked
	 * @throw FormulaException an exception when error occurs
	 */
	abstract public void checkArgument(Expression arg) throws FormulaException;
	
	/**
	 * Choice function
	 * 
	 * Make choice between 2 expressions with a condition expression. 
	 * <p>Syntax:</p>
	 * <p>
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * choice(bool_expr,expr1,expr2)
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * </p>
	 * <p>return:if bool_expr is true ,expr1 otherwise expr2.
	 * 
	 * @author duanyy
	 * @version 1.0.0
	 */
	public static class Choice extends Function{
		/**
		 * constructor
		 */
		public Choice() {
			super("choice");
		}

		
		public void checkArgument(Expression arg) throws FormulaException {
			if (getArgumentCount() > 3){
				throw new FormulaException("choice function only supports 3 arguments.");
			}
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			if (getArgumentCount() != 3){
				throw new FormulaException("choice function need 3 arguments.we have " + getArgumentCount());
			}
			
			Expression boolExpr = getArgument(0);
			
			ExprValue boolValue = boolExpr.getValue(provider);
			if (boolValue.getBoolean()){
				return getArgument(1).getValue(provider);
			}else{
				return getArgument(2).getValue(provider);
			}
		}
	}
	
	/**
	 * Nvl
	 * 
	 * <br>
	 * if a expr is null,give a default value.
	 * <p>Syntax:</p>
	 * <p>
	 * ~~~~~~~~~~~~~~~~~~~~~~~~
	 * nvl(expr,default_expr)
	 * ~~~~~~~~~~~~~~~~~~~~~~~~
	 * </p>
	 * <p>return:expr if expr is not null,or default_expr
	 * @author duanyy
	 * @version 1.0.0
	 */
	public static class Nvl extends Function{

		public Nvl() {
			super("nvl");
		}

		
		public void checkArgument(Expression arg) throws FormulaException {
			if (getArgumentCount() > 2){
				throw new FormulaException("nvl function only supports 2 arguments.");
			}	
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			if (getArgumentCount() != 2){
				throw new FormulaException("nvl function need 2 arguments.we have " + getArgumentCount());
			}
			
			ExprValue value = getArgument(0).getValue(provider);
			if (value == null){
				value = getArgument(1).getValue(provider);
			}
			return value;
		}
		
	}
	
	/**
	 * Parse string value into date value
	 * 
	 * <br>
	 * Parse string value into date value with a date pattern.The default pattern is 'yyyyMMddHHmmss'
	 * <p>Syntax:</p>
	 * <p>
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * to_date(str_expr[,pattern])
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * </p>
	 * <p>return:date_expr
	 * @author duanyy
	 * @version 1.0.0
	 */
	public static class Char2Date extends Function{
		public Char2Date() {
			super("to_date");
			// TODO Auto-generated constructor stub
		}

		
		public void checkArgument(Expression arg) throws FormulaException {
			if (getArgumentCount() > 2){
				throw new FormulaException("to_date function only supports 1 or 2 arguments.");
			}			
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			if (getArgumentCount() < 1){
				throw new FormulaException("to_date function need at least 1 argument.we have " + getArgumentCount());
			}
			
			String pattern = "yyyyMMddHHmmss";
			if (getArgument(1) != null){
				pattern = getArgument(1).getValue(provider).getString();
			}
			
			String value = getArgument(0).getValue(provider).getString();
			return new ExprValue(DateUtil.parseDate(value, pattern));
		}
	}
	/**
	 * Format date value to string value
	 * 
	 * <br>Format date value to string value with a date pattern.The default pattern is 'yyyyMMddHHmmss'
	 * <p>Syntax:</p>
	 * <p>
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * to_char(date_expr[,pattern])
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * </p>
	 * <p>return:string_expr
	 * @author duanyy
	 * @version 1.0.0
	 */
	public static class Date2Char extends Function{
		public Date2Char() {
			super("to_char");
			// TODO Auto-generated constructor stub
		}

		
		public void checkArgument(Expression arg) throws FormulaException {
			if (getArgumentCount() > 2){
				throw new FormulaException("to_char function only supports 1 or 2 arguments.");
			}			
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			if (getArgumentCount() < 1){
				throw new FormulaException("to_char function need at least 1 argument.we have " + getArgumentCount());
			}
			
			String pattern = "yyyyMMddHHmmss";
			if (getArgument(1) != null){
				pattern = getArgument(1).getValue(provider).getString();
			}
			
			Date value = getArgument(0).getValue(provider).getDate();
			
			return new ExprValue(DateUtil.formatDate(value, pattern));
		}		
	}
	
	/**
	 * Convert string value to long value
	 * 
	 * <br>Convert string value to long value.
	 * <p>Syntax:</p>
	 * <p>
	 * ~~~~~~~~~~~~~~~~~~~~~
	 * to_long(string_expr)
	 * ~~~~~~~~~~~~~~~~~~~~~
	 * </p>
	 * <p>return:long_expr
	 * @author duanyy
	 * @version 1.0.0
	 */	
	public static class ToLong extends Function{

		public ToLong() {
			super("to_long");
		}

		
		public void checkArgument(Expression arg) throws FormulaException {
			if (getArgumentCount() > 1){
				throw new FormulaException("to_long function only supports 1 argument.");
			}	
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			if (getArgumentCount() != 1){
				throw new FormulaException("to_long function need 1 argument.");
			}
			
			String value = getArgument(0).getValue(provider).getString();
			return new ExprValue(Long.valueOf(value));
		}
	}
	
	/**
	 * Convert string value to double value
	 * 
	 * Convert string value to double value.
	 * <p>Syntax:</p>
	 * <p>
	 * ~~~~~~~~~~~~~~~~~~~~~~
	 * to_double(string_expr)
	 * ~~~~~~~~~~~~~~~~~~~~~~
	 * </p>
	 * <p>return:double_expr
	 * 
	 * @author duanyy
	 * @version 1.0.0
	 */	
	public static class ToDouble extends Function{

		public ToDouble() {
			super("to_double");
		}

		
		public void checkArgument(Expression arg) throws FormulaException {
			if (getArgumentCount() > 1){
				throw new FormulaException("to_double function only supports 1 argument.");
			}	
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			if (getArgumentCount() != 1){
				throw new FormulaException("to_double function need 1 argument.");
			}
			
			String value = getArgument(0).getValue(provider).getString();
			return new ExprValue(Double.valueOf(value));
		}
	}
	/**
	 * Convert other value to string value
	 * 
	 * <br>Convert other value to string value.
	 * <p>Syntax:</p>
	 * <p>
	 * ~~~~~~~~~~~~~~~~~~
	 * to_string(expr)
	 * ~~~~~~~~~~~~~~~~~~
	 * </p>
	 * <p>return:string_expr
	 * @author duanyy
	 * @version 1.0.0
	 */	
	public static class ToString extends Function{

		public ToString() {
			super("to_string");
		}

		
		public void checkArgument(Expression arg) throws FormulaException {
			if (getArgumentCount() > 1){
				throw new FormulaException("to_string function only supports 1 argument.");
			}	
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			if (getArgumentCount() != 1){
				throw new FormulaException("to_string function need 1 argument.");
			}
			
			String value = getArgument(0).getValue(provider).toString();
			return new ExprValue(value);
		}
	}	
	/**
	 * Get string length
	 * 
	 * <br>Get string length.
	 * <p>Syntax:</p>
	 * <p>
	 * ~~~~~~~~~~~~~~~~~
	 * strlen(str_expr)
	 * ~~~~~~~~~~~~~~~~~
	 * </p>
	 * <p>return:long
	 * @author duanyy
	 * @version 1.0.0
	 */	
	public static class StrLen extends Function{

		public StrLen() {
			super("strlen");
		}

		
		public void checkArgument(Expression arg) throws FormulaException {
			if (getArgumentCount() > 1){
				throw new FormulaException("strlen function only supports 1 argument.");
			}	
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			if (getArgumentCount() != 1){
				throw new FormulaException("strlen function need 1 argument.");
			}
			
			String value = getArgument(0).getValue(provider).toString();
			return new ExprValue(value.length());
		}
	}	
	/**
	 * Get sub string from source string
	 * 
	 * <br>Get sub string from source string
	 * <p>Syntax:</p>
	 * <p>
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * substr(src_str,start_offset,length)
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * </p>
	 * <p>return:substring
	 * @author duanyy
	 * @version 1.0.0
	 */	
	public static class SubStr extends Function{

		public SubStr() {
			super("substr");
		}

		
		public void checkArgument(Expression arg) throws FormulaException {
			if (getArgumentCount() > 3){
				throw new FormulaException("substr function only supports 3 argument.");
			}	
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			if (getArgumentCount() != 3){
				throw new FormulaException("substr function need 3 argument.");
			}
			
			String string = getArgument(0).getValue(provider).getString();
			int startOffset = getArgument(1).getValue(provider).getInt();
			int length = getArgument(2).getValue(provider).getInt();
			
			startOffset = startOffset < 0 ? 0 : startOffset >= string.length()?string.length() - 1:startOffset; 
			length = string.length() - startOffset < length ? string.length() - startOffset:length;
			return new ExprValue(string.substring(startOffset, startOffset + length));
		}
	}
	
	/**
	 * Find index in source string when child string is matched.
	 * 
	 * <br>Find index in source string when child string is matched.
	 * <p>Syntax:</p>
	 * <p>
	 * ~~~~~~~~~~~~~~~~~~~~~~~~
	 * instr(src_str,child_str)
	 * ~~~~~~~~~~~~~~~~~~~~~~~~
	 * </p>
	 * <p>return:index of child string,-1 if child string is not matched.
	 * @author duanyy
	 * @version 1.0.0
	 */	
	public static class InStr extends Function{

		public InStr() {
			super("instr");
		}

		
		public void checkArgument(Expression arg) throws FormulaException {
			if (getArgumentCount() > 2){
				throw new FormulaException("instr function only supports 2 argument.");
			}	
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			if (getArgumentCount() != 2){
				throw new FormulaException("substr function need 2 argument.");
			}
			
			String srcString = getArgument(0).getValue(provider).getString();
			String childString = getArgument(1).getValue(provider).getString();
			
			return new ExprValue(srcString.indexOf(childString));
		}
	}

	/**
	 * Test whether string matches the given regular expression.
	 * 
	 * Test whether string matches the given regular expression.
	 * <p>Syntax:</p>
	 * <p>
	 * ~~~~~~~~~~~~~~~~~~~~~~~~
	 * match(src_str,regex)
	 * ~~~~~~~~~~~~~~~~~~~~~~~~
	 * </p>
	 * <p>return:true when src_str matches regex.
	 * @author duanyy
	 * @version 1.0.0
	 */	
	public static class Match extends Function{

		public Match() {
			super("match");
		}

		
		public void checkArgument(Expression arg) throws FormulaException {
			if (getArgumentCount() > 2){
				throw new FormulaException("match function only supports 2 argument.");
			}	
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			if (getArgumentCount() != 2){
				throw new FormulaException("match function need 2 argument.");
			}
			
			String srcString = getArgument(0).getValue(provider).getString();
			String regex = getArgument(1).getValue(provider).getString();
			
			return new ExprValue(srcString.matches(regex));
		}
	}
}
