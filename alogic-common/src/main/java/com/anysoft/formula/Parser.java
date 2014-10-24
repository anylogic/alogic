package com.anysoft.formula;


/**
 * Formula Parser
 * 
 * <p>AnyFormula是纯Java实现的公式解析器，具备下列特性：<br>
 * - 支持加,减,乘,除,模等算术操作符<br>
 * - 支持大于,小于,等于等逻辑操作符<br>
 * - 支持下列内置函数<br>
 *   + choice:{@link Function#Choice}<br>
 *   + nvl:{@link Function#Nvl}<br>
 *   + to_date:{@link Function#Char2Date}<br>
 *   + to_char:{@link Function#Date2Char}<br>
 *   + to_string:{@link Function#ToString}<br>
 *   + to_long:{@link Function#ToLong}<br>
 *   + to_double:{@link Function#ToDouble}<br>
 *   + substr:{@link Function#SubStr}<br>
 *   + instr:{@link Function#InStr}<br>
 *   + strlen:{@link Function#StrLen}<br>
 *   + match:{@link Function#Match}<br>
 * - 通过{@link FunctionHelper}机制支持自定义函数插件<br>
 * - 通过{@link DataProvider}机制支持自定义变量<br>
 * 
 * @author duanyy
 * @version 1.0
 */
public class Parser {
	/**
	 * 当前处理的公式文本
	 */
	protected String text;
	
	/**
	 * 当前解析的位置
	 */
	protected int current;
	
	public static final char ADD_OR_POSITIVE = '+';
	public static final char SUBTRACT_OR_NEGATIVE = '-';
	public static final char MULTIPLY = '*';
	public static final char DIVIDE = '/';
	public static final char MOD = '%';
	public static final char EQUAL = '=';
	public static final char LESS = '<';
	public static final char GREATER = '>';
	public static final char BIT_AND = '&';
	public static final char BIT_OR = '|';
	public static final char LEFT_BRACKET = '(';
	public static final char RIGHT_BRACKET = ')';
	public static final char PARAMETER_SEPERATOR = ',';
	public static final char NOT = '!';
	public static final char STRING = '\'';
	public static final char IDENTIFIER = 257;
	public static final char FINISHED = 259;
	public static final char EQUAL_LESS = 260;
	public static final char EQUAL_GREATE = 261;
	public static final char NOT_EQUAL = 262;
	public static final char AND = 263;
	public static final char OR = 264;
	public static final char FLOAT = 265;
	public static final char INTEGER = 266;
	
	/**
	 * 当前解析Token的类型
	 */
	protected char type;
	
	/**
	 * 当前解析的ID
	 */
	protected String identifier;
	
	/**
	 * 当前是否在字符串中
	 */
	protected boolean inString;
	
	/**
	 * 函数对象辅助器
	 */
	protected FunctionHelper funcHelper = null;
	
	/**
	 * to set function helper
	 * @param helper helper
	 */
	public void setFunctionHelper(FunctionHelper helper){
		funcHelper = helper;
	}
	
	static public boolean isSpace(char ch){
		return Character.isSpaceChar(ch);
	}
	static public boolean isAlpha(char ch){
		return ch >= 'a' && ch <='z' || ch >= 'A' && ch <= 'Z';
	}
	static public boolean isDigital(char ch){
		return ch >= '0' && ch <= '9';
	}
	
	static public boolean isIdentifierChar(char ch){
		return isAlpha(ch) || isDigital(ch) || ch == '_' || ch == '.';
	}
	
	static public boolean isIdentifierFirstChar(char ch){
		return isAlpha(ch) || ch == '_' || ch == '.';
	}
	
	static public boolean isDecimalChar(char ch){
		return ch == '.';
	}
	
	public Parser(){
		
	}
	public Parser(FunctionHelper helper){
		funcHelper = helper;
	}
	
	/**
	 * 将公式文本解析为Expression模型
	 * @param _text 公式文本
	 * @return Expression实例
	 * @throws FormulaException
	 */
	public Expression parse(String _text)throws FormulaException{
		text = _text;
		current = 0;
		inString = false;
		identifier = "";
		lookAhead();
		return expression_R_Logical_Or
				(
				expression_R_Logical_And
					(
					expression_R_Compare
						(
							expression_R(higher_expression())
						)
					)
				);
	}
	
	private Expression higher_expression() {
		return higher_expression_R( sign_expression() );
	}

	private Expression higher_expression_R(Expression left) {
		Expression result = left;
		if( type == MULTIPLY )
		{
			match( MULTIPLY );
			Expression higher = sign_expression();
			result = higher_expression_R( Expression.BinaryExpression.createChild(Expression.Operator.OP_Mul,left,higher) );
		}
		else if( type == DIVIDE )
		{
			match( DIVIDE );
			Expression higher = sign_expression() ;
			result = higher_expression_R( Expression.BinaryExpression.createChild(Expression.Operator.OP_Div,left,higher) );
		}
		return result;
	}

	private Expression sign_expression() {
		Expression result;
		if( type == ADD_OR_POSITIVE )
		{
			match( ADD_OR_POSITIVE );
			result = sign_expression();
		}
		else if( type == SUBTRACT_OR_NEGATIVE )
		{
			match( SUBTRACT_OR_NEGATIVE );
			result = new Expression.UnaryExpression(Expression.Operator.OP_Negative,sign_expression());
		}
		else
			result = factor();
		return result;
	}

	private Expression factor() {
		Expression result;
		if (type == FLOAT)
		{
			match(FLOAT);
			result = new Expression.DoubleConstant(Double.valueOf(identifier));
		}
		else
			if (type == STRING)
			{
				match(STRING);
				result = new Expression.StringConstant(identifier);
			}
			else
			if (type == INTEGER)
			{
				match(INTEGER);
				result = new Expression.LongConstant(Long.valueOf(identifier));
			}
			else
				if (type == LEFT_BRACKET)
				{
					match(LEFT_BRACKET);
					result = expression_R_Logical_Or
				(
				expression_R_Logical_And
					(
					expression_R_Compare
						(
							expression_R(higher_expression())
						)
					)
				);
					match( RIGHT_BRACKET );
				}
			else
			{
				result = lang_structure();
			}
		return result;
	}

	private Expression lang_structure() {
	    String id = identifier;
        match(IDENTIFIER);
        return lang_tail( id );
	}

	private Expression lang_tail(String id) {
		if (id.equals("true"))
		{
			return new Expression.BooleanConstant(true);
		}
		if (id.equals("false"))
		{
			return new Expression.BooleanConstant(false);
		}

		if (type == LEFT_BRACKET)
		{		
			Function result = null;
			if (funcHelper != null)
			{
				result = (Function)funcHelper.customize(id);
			}
			if (result != null)
			{
				match( LEFT_BRACKET );
				result.addArgument(expression_R_Logical_Or
				(
				expression_R_Logical_And
					(
					expression_R_Compare
						(
							expression_R(higher_expression())
						)
					)
				));
				for(;;)
				{
					if( type == RIGHT_BRACKET )
					{
						match( RIGHT_BRACKET );
						break;
					}
					match(PARAMETER_SEPERATOR);
					result.addArgument(expression_R_Logical_Or
				(
				expression_R_Logical_And
					(
					expression_R_Compare
						(
							expression_R(higher_expression())
						)
					)
				));	
				}
				return result;
			}

			return  expression_R_Logical_Or
				(
				expression_R_Logical_And
					(
					expression_R_Compare
						(
							expression_R(higher_expression())
						)
					)
				);
		}
		return new Expression.Variant(id);
	}

	private Expression expression_R_Logical_Or(Expression left) {
		Expression result = left;

		if( type == OR )
		{
			match( OR );
			Expression higher = expression_R_Logical_And
					(
					expression_R_Compare
						(
							expression_R(higher_expression())
						)
					);
			result = expression_R_Logical_Or
				(Expression.BinaryExpression.createChild(Expression.Operator.OP_Or,left,higher));
		}
		return result;
	}

	private Expression expression_R_Logical_And(Expression left) {
		Expression result = left;

		if( type == AND )
		{
			match( AND );
			Expression  higher = expression_R_Compare
						(
							expression_R(higher_expression())
						);
			result = expression_R_Logical_And
				(Expression.BinaryExpression.createChild(Expression.Operator.OP_And,left,higher));
		}
		return result;
	}

	private Expression expression_R_Compare(Expression left) {
		Expression result = left;

		if( type == EQUAL_LESS )
		{
			match( EQUAL_LESS );
			Expression higher = expression_R(higher_expression());
			result = expression_R_Compare(
				Expression.BinaryExpression.createChild(Expression.Operator.OP_Less_Equal,left,higher));
		}
		else if( type == EQUAL_GREATE )
		{
			match( EQUAL_GREATE );
			Expression higher = expression_R(higher_expression());
			result = expression_R_Compare(Expression.BinaryExpression.createChild(Expression.Operator.OP_Greater_Equal,left,higher));
		}
		else if( type == NOT_EQUAL )
		{
			match( NOT_EQUAL );
			Expression higher = expression_R(higher_expression());
			result = expression_R_Compare(Expression.BinaryExpression.createChild(Expression.Operator.OP_Not_Equal,left,higher));
		}
		else if( type == EQUAL )
		{
			match( EQUAL );
			Expression higher = expression_R(higher_expression());
			result = expression_R_Compare(Expression.BinaryExpression.createChild(Expression.Operator.OP_Equal,left,higher));
		}
		else if( type == LESS )
		{
			match( LESS );
			Expression higher = expression_R(higher_expression());
			result = expression_R_Compare(Expression.BinaryExpression.createChild(Expression.Operator.OP_Less,left,higher));
		}
		else if( type == GREATER )
		{
			match( GREATER );
			Expression higher = expression_R(higher_expression());
			result = expression_R_Compare(Expression.BinaryExpression.createChild(Expression.Operator.OP_Greater,left,higher));
		}
		return result;
	}

	private Expression expression_R(Expression left) {
		Expression result = left;

		if( type == ADD_OR_POSITIVE )
		{
			match( ADD_OR_POSITIVE );
			Expression higher = higher_expression();
			result = expression_R(Expression.BinaryExpression.createChild(Expression.Operator.OP_Add,left,higher));
		}
		else if( type == SUBTRACT_OR_NEGATIVE )
		{
			match( SUBTRACT_OR_NEGATIVE );
			Expression higher = higher_expression();
			result = expression_R(Expression.BinaryExpression.createChild(Expression.Operator.OP_Sub,left,higher));
		}
		return result;
	}

	private char get(int _current){
		if (_current >= text.length()){
			return 0;
		}
		return text.charAt(_current);
	}
	
	private void lookAhead(){
		for (;;){
			if (inString){
				if (get(current) != STRING){
					identifier += get(current);
					++current;
					continue;
				}
			}
			if (isSpace(get(current))){
				++current;
				continue;
			}
			if (get(current) == STRING){
				type = STRING;
				++current;
				if (!inString){
					identifier = "";
					inString = true;
					continue;
				}else{
					inString = false;
					break;
				}
			}
			
			if (get(current) == LESS && get(current + 1) == EQUAL){
				type = EQUAL_LESS;
				current += 2;
			}else{
				if (get(current) == GREATER && get(current + 1) == EQUAL){
					type = EQUAL_GREATE;
					current += 2;
				}else{
					if (get(current) == EQUAL && get(current + 1) == EQUAL){
						type = EQUAL;
						current += 2;
					}else{
						if (get(current) == NOT && get(current + 1) == EQUAL){
							type = NOT_EQUAL;
							current += 2;							
						}else{
							if (get(current) == BIT_AND && get(current + 1) == BIT_AND){
								type = AND;
								current += 2;	
							}else{
								if (get(current) == BIT_OR && get(current + 1) == BIT_OR){
									type = OR;
									current += 2;										
								}else{
									char ch = get(current);
									if (ch == ADD_OR_POSITIVE
											|| ch == SUBTRACT_OR_NEGATIVE
											|| ch == MULTIPLY
											|| ch == DIVIDE
											|| ch == MOD
											|| ch == LEFT_BRACKET
											|| ch == RIGHT_BRACKET
											|| ch == PARAMETER_SEPERATOR
											|| ch == EQUAL
											|| ch == LESS
											|| ch == GREATER){
										type = ch;
										++current;
									}else{
										if (isIdentifierFirstChar(get(current))){
											type = IDENTIFIER;
											identifier = "";
											identifier += get(current);
											++current;
											while(isIdentifierChar(get(current))){
												identifier += get(current);
												++current;
											}
										}else{
											if (get(current) == 0){
												type = FINISHED;
											}else{
												identifier = "";
												identifier += get(current);
												++current;
												boolean isFloat = false;
												while((isDecimalChar(get(current)) && !isFloat) || isDigital(get(current)) ){
													identifier += get(current);
													if (isDecimalChar(get(current))){
														isFloat = true;
													}
													++current;
												}
												type = isFloat?FLOAT:INTEGER;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			break;
		}
	}
	
	private void match(char _type){
		if( type == _type )
			lookAhead();
		else
			throw new FormulaException("mismatched token,index:" + current);
	}
}
