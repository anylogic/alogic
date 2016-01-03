package com.anysoft.formula;

import com.anysoft.formula.ExprValue.DataType;

/**
 * Expression
 * @author duanyy
 * @version 1.0.0
 * 
 * @version 1.6.4.21 [20151229 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 */
public abstract class Expression {

	/**
	 * operator of {@code Expression}
	 */
	protected Operator operator;
	/**
	 * construction
	 * @param oper
	 *            operator of {@code Expression}
	 */
	public Expression(Operator oper) {
		operator = oper;
	}

	/**
	 *  to compute the expression with {@code DataProvider}
	 *  @param provider instance of DataProvider
	 *  @return value of expr
	 *  @throws FormulaException
	 */
	public abstract ExprValue getValue(DataProvider provider)
			throws FormulaException; // NOSONAR

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
		OP_Add, // + // NOSONAR
		OP_Sub, // - // NOSONAR
		OP_Mul, // * // NOSONAR
		OP_Div, // / // NOSONAR
		OP_Mod, // % // NOSONAR
		OP_Greater, // > // NOSONAR
		OP_Greater_Equal, // >= // NOSONAR
		OP_Equal, // = // NOSONAR
		OP_Not_Equal, // != // NOSONAR
		OP_Less, // < // NOSONAR
		OP_Less_Equal, // <= // NOSONAR
		OP_And, // && // NOSONAR
		OP_Or, // || // NOSONAR
		OP_Not, // ! // NOSONAR
		OP_Extend, // extend ,like function // NOSONAR
		OP_Float_Constant, OP_Integer_Constant, OP_Boolean_Constant, OP_String_Constant, OP_Variant, OP_Negative, OP_Positive // NOSONAR
	}

	/**
	 * Binary Expression
	 * @author duanyy
	 * @version 1.0.0
	 */
	public abstract static class BinaryExpression extends Expression {
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
		 * @param oper operator
 		 * @param leftExpr left expr
		 * @param rightExpr right expr
		 */
		public BinaryExpression(Operator oper, Expression leftExpr,
				Expression rightExpr) {
			super(oper);
			left = leftExpr;
			right = rightExpr;
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
		@Override
		public String toString() {
			return "(" + left.toString() + getOperatorPrototype()
					+ right.toString() + ")";
		}

		/**
		 * Create a binary expression
		 * @param oper
		 *            the operator
		 * @param leftExpr
		 *            the left expression
		 * @param rightExpr
		 *            the right expression
		 * @return new binary expression
		 * @throws FormulaException
		 */
		public static Expression createChild(Operator oper, // NOSONAR
				Expression leftExpr, Expression rightExpr) {
			switch (oper) {
			case OP_Add:
			case OP_Sub:
			case OP_Mul:
			case OP_Div:
			case OP_Mod:
				return new ArithmeticExpression(oper, leftExpr, rightExpr);
			case OP_Greater:
			case OP_Greater_Equal:
			case OP_Equal:
			case OP_Not_Equal:
			case OP_Less:
			case OP_Less_Equal:
			case OP_Or:
			case OP_And:
			case OP_Not:
				return new LogicalExpression(oper, leftExpr, rightExpr);
			default:
			}
			throw new FormulaException("Unsupport operator:" + oper); // NOSONAR
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
		 * @param oper logical operator
		 * @param leftExpr left expr
		 * @param rightExpr right expr
		 */
		public LogicalExpression(Operator oper, Expression leftExpr,
				Expression rightExpr) {
			super(oper, leftExpr, rightExpr);
		}

		/**
		 * to get the prototype of operator
		 */
		@Override
		public String getOperatorPrototype() { // NOSONAR
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
		@Override
		public ExprValue getValue(DataProvider provider){ // NOSONAR
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
		 * @param oper operator of expr
		 * @param leftExpr left expr
		 * @param rightExpr right expr
		 */
		public ArithmeticExpression(Operator oper, Expression leftExpr,
				Expression rightExpr) {
			super(oper, leftExpr, rightExpr);
		}

		/**
		 *  to compute the expression with {@code DataProvider}
		 *  @param provider instance of DataProvider
		 *  @return value of expr
		 */
		@Override
		public ExprValue getValue(DataProvider provider){ // NOSONAR
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
		@Override
		public String getOperatorPrototype() { // NOSONAR
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
	public static class UnaryExpression extends Expression {
		/**
		 * the child 
		 */
		protected Expression expr = null;	
		
		/**
		 * constructor
		 * @param oper the operator
		 * @param child the child expr
		 */
		public UnaryExpression(Operator oper,Expression child) {
			super(oper);
			expr = child;
		}
		/**
		 *  to compute the expression with {@code DataProvider}
		 *  @param provider instance of DataProvider
		 *  @return value of expr
		 */
		@Override
		public ExprValue getValue(DataProvider provider){ // NOSONAR
			switch (operator){
				case OP_Negative:{ // NOSONAR
					ExprValue value = expr.getValue(provider);
					switch (value.getDataType()){
						case Long:
							return new ExprValue(-value.getLong());
						case Double:
							return new ExprValue(-value.getDouble());
						default:
					}
					throw new FormulaException("Can not get a negative value of " + value.getDataType());
				}
				case OP_Positive:
					return expr.getValue(provider);
				case OP_Not:{ // NOSONAR
					ExprValue value = expr.getValue(provider);
					if (value.getDataType() == DataType.Boolean){
						return new ExprValue(!value.getBoolean());
					}
					throw new FormulaException("Can not get a negative value of " + value.getDataType());	
				}
				default:
			}
			throw new FormulaException("Unsupported operator:" + operator.toString());
		}
		/**
		 * to String
		 */
		@Override
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
		@Override
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
	public abstract static class Constant extends Expression{
		public Constant(Operator oper) {
			super(oper);
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
		 * @param v value
 		 */
		public StringConstant(String v) {
			super(Operator.OP_String_Constant);
			value = v;
		}

		@Override
		public ExprValue getValue(DataProvider provider){
			return new ExprValue(value);
		}

		@Override
		public String getOperatorPrototype() {
			return value;
		}
		
		/**
		 * to String
		 */
		@Override
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
		public LongConstant(long v) {
			super(Operator.OP_Integer_Constant);
			value = v;
		}

		@Override
		public ExprValue getValue(DataProvider provider){
			return new ExprValue(value);
		}

		@Override
		public String getOperatorPrototype() {
			return String.valueOf(value);
		}
		
		@Override
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
		public DoubleConstant(double v) {
			super(Operator.OP_Float_Constant);
			value = v;
		}

		@Override
		public ExprValue getValue(DataProvider provider) {
			return new ExprValue(value);
		}

		@Override
		public String getOperatorPrototype() {
			return String.valueOf(value);
		}
		@Override
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
		public BooleanConstant(boolean v) {
			super(Operator.OP_Boolean_Constant);
			value = v;
		}

		@Override
		public ExprValue getValue(DataProvider provider){
			return new ExprValue(value);
		}

		@Override
		public String getOperatorPrototype() {
			return String.valueOf(value);
		}
		@Override
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
		public Variant(String vName) {
			super(Operator.OP_Variant);
			varName = vName;
		}

		@Override
		public ExprValue getValue(DataProvider provider){
			if (provider == null){
				throw new FormulaException("Data provider is null,can not get value of " + varName);
			}
			if (varContext == null){
				varContext = provider.getContext(varName);
			}
			String value = provider.getValue(varName, varContext, null);
			return value != null ? new ExprValue(value): null;
		}

		@Override
		public String getOperatorPrototype() {
			return varName;
		}
		
		@Override
		public String toString(){
			return varName;
		}		
	}
}
