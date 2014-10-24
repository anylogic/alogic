package com.anysoft.formula;

/**
 * Expression
 * @author duanyy
 * @version 1.0.0
 */
abstract public class Expression {

	/**
	 * construction
	 * @param _operator
	 *            operator of {@code Expression}
	 */
	public Expression(Operator _operator) {
		operator = _operator;
	}

	/**
	 *  to compute the expression with {@code DataProvider}
	 *  @param provider instance of DataProvider
	 *  @return value of expr
	 *  @throws FormulaException
	 */
	public abstract ExprValue getValue(DataProvider provider)
			throws FormulaException;

	/**
	 * to get the prototype of operator
	 * @return prototype
	 */
	public abstract String getOperatorPrototype();
	
	/**
	 * get operator
	 * @return operator
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * Operator
	 * @author duanyy
	 *
	 */
	public enum Operator {
		OP_Add, // +
		OP_Sub, // -
		OP_Mul, // *
		OP_Div, // /
		OP_Mod, // %
		OP_Greater, // >
		OP_Greater_Equal, // >=
		OP_Equal, // =
		OP_Not_Equal, // !=
		OP_Less, // <
		OP_Less_Equal, // <=
		OP_And, // &&
		OP_Or, // ||
		OP_Not, // !
		OP_Extend, // extend ,like function
		OP_Float_Constant, OP_Integer_Constant, OP_Boolean_Constant, OP_String_Constant, OP_Variant, OP_Negative, OP_Positive
	}

	/**
	 * operator of {@code Expression}
	 */
	protected Operator operator;

	/**
	 * Binary Expression
	 * @author duanyy
	 * @version 1.0.0
	 */
	static abstract public class BinaryExpression extends Expression {
		/**
		 * the left expression
		 */
		protected Expression left = null;
		/**
		 * the right expression
		 */
		protected Expression right = null;

		/**
		 * Constructor
		 * @param _operator operator
 		 * @param _left left expr
		 * @param _right right expr
		 */
		public BinaryExpression(Operator _operator, Expression _left,
				Expression _right) {
			super(_operator);
			left = _left;
			right = _right;
		}

		/**
		 * to get the left expr
		 * @return expr
		 */
		public Expression getLeft() {
			return left;
		}
		/**
		 * to get the right expr
		 * @return expr
		 */
		public Expression getRight() {
			return right;
		}
		
		/**
		 * to string
		 */
		public String toString() {
			return "(" + left.toString() + getOperatorPrototype()
					+ right.toString() + ")";
		}

		/**
		 * Create a binary expression
		 * @param _operator
		 *            the operator
		 * @param _left
		 *            the left expression
		 * @param _right
		 *            the right expression
		 * @return new binary expression
		 * @throws FormulaException
		 */
		public static Expression createChild(Operator _operator,
				Expression _left, Expression _right) throws FormulaException {
			switch (_operator) {
			case OP_Add:
			case OP_Sub:
			case OP_Mul:
			case OP_Div:
			case OP_Mod:
				return new ArithmeticExpression(_operator, _left, _right);
			case OP_Greater:
			case OP_Greater_Equal:
			case OP_Equal:
			case OP_Not_Equal:
			case OP_Less:
			case OP_Less_Equal:
			case OP_Or:
			case OP_And:
			case OP_Not:
				return new LogicalExpression(_operator, _left, _right);
			default:
			}
			throw new FormulaException("Unsupport operator:" + _operator);
		}

	}

	/**
	 * logical exprssion
	 * @author duanyy
	 *
	 */
	public static class LogicalExpression extends BinaryExpression {

		/**
		 * constructor
		 * 
		 * @param _operator logical operator
		 * @param _left left expr
		 * @param _right right expr
		 */
		public LogicalExpression(Operator _operator, Expression _left,
				Expression _right) {
			super(_operator, _left, _right);
		}

		/**
		 * to get the prototype of operator
		 */
		
		public String getOperatorPrototype() {
			switch (operator) {
			case OP_Greater:
				return ">";
			case OP_Greater_Equal:
				return ">=";
			case OP_Equal:
				return "==";
			case OP_Not_Equal:
				return "!=";
			case OP_Less:
				return "<";
			case OP_Less_Equal:
				return "<=";
			case OP_Or:
				return "||";
			case OP_And:
				return "&&";
			default:
			}

			return "";
		}

		/**
		 * to compute the expression with {@code DataProvider}
		 * @param provider instance of DataProvider
		 * @return value of expr
		 */
		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			// TODO Auto-generated method stub
			switch (operator) {
			case OP_Greater:
				return new ExprValue(left.getValue(provider).compareTo(
						right.getValue(provider)) > 0);
			case OP_Greater_Equal:
				return new ExprValue(left.getValue(provider).compareTo(
						right.getValue(provider)) >= 0);
			case OP_Equal:
				return new ExprValue(left.getValue(provider).compareTo(
						right.getValue(provider)) == 0);
			case OP_Not_Equal:
				return new ExprValue(left.getValue(provider).compareTo(
						right.getValue(provider)) != 0);
			case OP_Less:
				return new ExprValue(left.getValue(provider).compareTo(
						right.getValue(provider)) < 0);
			case OP_Less_Equal:
				return new ExprValue(left.getValue(provider).compareTo(
						right.getValue(provider)) <= 0);
			case OP_Or:
				return new ExprValue(left.getValue(provider).getBoolean()
						|| right.getValue(provider).getBoolean());
			case OP_And:
				return new ExprValue(left.getValue(provider).getBoolean()
						&& right.getValue(provider).getBoolean());
			default:
			}
			throw new FormulaException("Unsupport operator:" + operator);
		}

	}

	/**
	 * Arithmetic Expression
	 * @author duanyy
	 *
	 */
	public static class ArithmeticExpression extends BinaryExpression {
		/**
		 * constructor 
		 * @param _operator operator of expr
		 * @param _left left expr
		 * @param _right right expr
		 */
		public ArithmeticExpression(Operator _operator, Expression _left,
				Expression _right) {
			super(_operator, _left, _right);
		}

		/**
		 *  to compute the expression with {@code DataProvider}
		 *  @param provider instance of DataProvider
		 *  @return value of expr
		 */
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			switch (operator) {
			case OP_Add:
				return left.getValue(provider).add(right.getValue(provider));
			case OP_Sub:
				return left.getValue(provider).sub(right.getValue(provider));
			case OP_Mul:
				return left.getValue(provider).mul(right.getValue(provider));
			case OP_Div:
				return left.getValue(provider).div(right.getValue(provider));
			case OP_Mod:
				return left.getValue(provider).mod(right.getValue(provider));
			default:
			}
			throw new FormulaException("Unsupport operator:" + operator);
		}

		/**
		 * to get the prototype of the operator
		 */
		
		public String getOperatorPrototype() {
			switch (operator) {
			case OP_Add:
				return "+";
			case OP_Sub:
				return "-";
			case OP_Mul:
				return "*";
			case OP_Div:
				return "/";
			case OP_Mod:
				return "%";
			default:
			}

			return "";
		}
	}
	
	/**
	 * Unary Expression
	 * @author duanyy
	 * 
	 */
	static public class UnaryExpression extends Expression {
		/**
		 * the child 
		 */
		protected Expression expr = null;	
		
		/**
		 * constructor
		 * @param _operator the operator
		 * @param _expr the child expr
		 */
		public UnaryExpression(Operator _operator,Expression _expr) {
			super(_operator);
			expr = _expr;
		}
		/**
		 *  to compute the expression with {@code DataProvider}
		 *  @param provider instance of DataProvider
		 *  @return value of expr
		 */
		
		public ExprValue getValue(DataProvider provider) throws FormulaException{
			switch (operator){
				case OP_Negative:{
					ExprValue _value = expr.getValue(provider);
					switch (_value.getDataType()){
						case Long:
							return new ExprValue(-_value.getLong());
						case Double:
							return new ExprValue(-_value.getDouble());
						default:
					}
					throw new FormulaException("Can not get a negative value of " + _value.getDataType());
				}
				case OP_Positive:
					return expr.getValue(provider);
				case OP_Not:{
					ExprValue _value = expr.getValue(provider);
					switch (_value.getDataType()){
						case Boolean:
							return new ExprValue(!_value.getBoolean());
						default:
					}
					throw new FormulaException("Can not get a negative value of " + _value.getDataType());	
				}
				default:
			}
			throw new FormulaException("Unsupported operator:" + operator.toString());
		}
		/**
		 * to String
		 */
		public String toString(){
			switch (operator){
			case OP_Negative:return "-(" + expr.toString() + ")";
			case OP_Positive:return "+(" + expr.toString() + ")";
			case OP_Not:return "!(" + expr.toString() + ")";
			default:
			}
			return "";
		}

		/**
		 * to get the prototype of operator
		 */
		
		public String getOperatorPrototype() {
			switch (operator){
			case OP_Negative:return "-";
			case OP_Positive:return "+";
			case OP_Not:return "!";
			default:
			}
			return "";
		}
	}
	
	/**
	 * Constant Expression
	 * @author duanyy
	 *
	 */
	abstract public static class Constant extends Expression{
		public Constant(Operator _operator) {
			super(_operator);
		}
	}
	
	/**
	 * String constant
	 * @author duanyy
	 *
	 */
	public static class StringConstant extends Constant{
		/**
		 * value
		 */
		protected String value;
		
		/**
		 * constructor
		 * @param _value value
 		 */
		public StringConstant(String _value) {
			super(Operator.OP_String_Constant);
			value = _value;
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			return new ExprValue(value);
		}

		
		public String getOperatorPrototype() {
			return value;
		}
		
		/**
		 * to String
		 */
		public String toString(){
			return "'" + value + "'";
		}
	}
	
	/**
	 * Long constant
	 * @author duanyy
	 *
	 */
	public static class LongConstant extends Constant{
		protected long value;
		public LongConstant(long _value) {
			super(Operator.OP_Integer_Constant);
			value = _value;
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			return new ExprValue(value);
		}

		
		public String getOperatorPrototype() {
			return String.valueOf(value);
		}
		
		public String toString(){
			return String.valueOf(value);
		}
	}
	
	/**
	 * double constant
	 * @author duanyy
	 *
	 */
	public static class DoubleConstant extends Constant{
		protected double value;
		public DoubleConstant(double _value) {
			super(Operator.OP_Float_Constant);
			value = _value;
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			return new ExprValue(value);
		}

		
		public String getOperatorPrototype() {
			return String.valueOf(value);
		}
		
		public String toString(){
			return String.valueOf(value);
		}
	}
	
	/**
	 * boolean constant
	 * @author duanyy
	 *
	 */
	public static class BooleanConstant extends Constant{
		protected boolean value;
		public BooleanConstant(boolean _value) {
			super(Operator.OP_Boolean_Constant);
			value = _value;
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			return new ExprValue(value);
		}

		
		public String getOperatorPrototype() {
			return String.valueOf(value);
		}
		
		public String toString(){
			return String.valueOf(value);
		}
	}
	
	/**
	 * Variant
	 * @author duanyy
	 * @version 1.0.0
	 * @version 1.0.1
	 * - @link com.anysoft.formula.DataProvider}进行了修改
	 */
	public static class Variant extends Expression{
		protected String varName;
		protected Object varContext = null;
		public Variant(String _varName) {
			super(Operator.OP_Variant);
			varName = _varName;
		}

		
		public ExprValue getValue(DataProvider provider)
				throws FormulaException {
			if (provider == null){
				throw new FormulaException("Data provider is null,can not get value of " + varName);
			}
			if (varContext == null){
				varContext = provider.getContext(varName);
			}
			String value = provider.getValue(varName, varContext, null);
			return value != null ? new ExprValue(value): null;
		}

		
		public String getOperatorPrototype() {
			return varName;
		}
		
		public String toString(){
			return varName;
		}		
	}
}
