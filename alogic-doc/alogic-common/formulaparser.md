## 公式解析器

公式解析器(com.anysoft.formula)是alogic自带的100%Java的解析器，其具备下列功能：

- 支持加,减,乘,除,模等算术操作符
- 支持大于,小于,等于等逻辑操作符
- 支持下列内置函数
	+ **choice**
	+ **nvl**
	+ **to_date**
	+ **to_char**
	+ **to_string**
	+ **to_long**
	+ **to_double**
	+ **substr**
	+ **instr**
	+ **strlen**
	+ **match**
- 通过FunctionHelper机制支持自定义函数插件
- 通过DataProvider机制支持自定义变量

## 更新历史
- alogic-1.3.39 [duanyy 20150812] 创建文档。

## 数据类型

公式解析器支持多种数据类型：

- **long**：整型，对应Java中的long;
- **double**：浮点型，对应Java中的double;
- **String**:字符串型，对应Java中的String;
- **Date**:日期型，对应Java中的java.util.Date;
- **Boolean**:布尔型，对应Java中的boolean.

可以进行进行算术符操作，例如：

```
	23 % 10 + (200 / 20 - 2*20) + 0.1 * 100
```

解析器对上面语句的解析模型为：

```
	(((23%10)+((200/20)-(2*20)))+(0.1*100))
```

可以进行逻辑操作，例如：

```
	1 < 2 && 2 > 3 && true
```
解析器对上面语句的解析模型为：

```
	(((1<2)&&(2>3))&&true)
```

可以进行字符串相加，例如:

```
	'hello ' + 'world' + 123
```

解析器对上面语句的解析模型为：

```
	(('hello '+'world')+123)
```

不同的数据类型之间还可以通过函数转换，如：
- to_date,字符串转日期
- to_char,日期转字符串
- to_string,其他类型转字符串
- to_long,字符串转整型
- to_double,字符串转浮点型

## 基本用法

下面看看如何使用公式解析器，先来一个简单的例子：
```
	String formula = "23 % 10 + (200 / 20 - 2*20) + 0.1 * 100";
	//声明一个Parser
	Parser parser = new Parser();	
	//通过Parser解析公式，得到表达式Expression	
	Expression expr = parser.parse(formula);
	
	//计算表达式
	ExprValue value = expr.getValue(null);
```
在上面的案例中，最后的ExprValue就是公式计算得到的值。

和一般的公式解析器不同，alogic-formula是编译型的，而不是解释性的。它首先通过parse解析出表达式，再通过表达式计算来得到最后的值，因此它支持
"一次编译，多次执行",在大量重复计算场合，会明显提升效率。

## 变量的使用

一个公式解析器如果只能解析静态的公式，那它的用处并不大，只有支持变量，才能带来该有的灵活性。

例如：
```
	1+2+to_long(id)
```
其中的id就是变量。

在实际使用者，可以传入不同的变量，从而计算出不同的结果。除了在公式中包含变量之外，还存在一个问题，那就是如果提供变量？我们采用的是DataProvider机制。

先看看DataProvider的定义:

```
	public interface DataProvider {
		/**
		 * 获取指定变量的值
		 * 
		 * @param varName 变量名
		 * @param context 上下文
		 * @param defaultValue 缺省值
		 * @return 变量值
		 */
		public String getValue(String varName,Object context,String defaultValue);
		
		/**
		 * 创建变量的上下文
		 * @param varName 变量名
		 * @return context 上下文对象
		 */
		public Object getContext(String varName);
	}
```

实现了DataProvider的类都可以作为变量提供者参与计算。

一个带变量的公式典型的处理过程如下：
```
	String formula = "23 % 10 + (to_long(id) / 20 - 2*20) + 0.1 * 100";
		
	Parser parser = new Parser();
	Expression expr = parser.parse(formula);
	
	//准备DataProvider
	DefaultProperties dataprovider = new DefaultProperties();
	//公式中的变量值设定为200
	dataprovider.SetValue("id", "200");
			
	//通过dataprovider来计算公式值
	ExprValue value = expr.getValue(dataprovider);
	System.out.println(value);
```

## 自定义函数

前文已经了解到alogic-formula提供了多种内置的函数，实际上alogic-formula是通过FunctionHelper的机制来实现函数的支持，并可以让开发者自定义函数。

FunctionHelper的定义如下：
```
	public interface FunctionHelper {
		public Expression customize(String funcName);
	}
```
Parser缺省使用了DefaultFunctionHelper来支持多种内置函数，开发者可以定制FunctionHelper链表来重用各种FunctionHelper。

下面是一个样例：
```
	//公式，注意公式中使用了一个age的函数
	String formula = "age() % 10 + (to_long(id) / 20 - 2*20) + 0.1 * 100";
	
	//定义age函数	
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
	
	//定义自己的FunctionHelper，只处理age函数	
	FunctionHelper myFunctionHelper = new FunctionHelper(){
		public Expression customize(String funcName) {
			if (funcName.equals("age")){
				return age;
			}
			return null;
		}			
	};
	
	//串联一个	DefaultFunctionHelper和一个myFunctionHelper，提供给Parser
	Parser parser = new Parser(new DefaultFunctionHelper(myFunctionHelper));
	Expression expr = parser.parse(formula);
		
	DefaultProperties dataprovider = new DefaultProperties();
	dataprovider.SetValue("id", "200");
			
	ExprValue value = expr.getValue(dataprovider);
	System.out.println(value);
```

## 附录：内置函数说明

### choice

通过判断条件在两个表达式之间做选择，类似于if..else...

语法：
```
	choice(bool_expr,expr1,expr2)
```
如果bool_expr为true，则返回expr1，否则返回expr2.

### nvl

如果一个表达式计算值为空，则取一个缺省的表达式计算值。

语法:
```
	nvl(expr,default_expr)
```
如果expr值为空，则返回default_expr

### to_date

解析一个字符串类型为日期型。

语法：
```
	to_date(str_expr[,pattern])
```
将表达式str_expr转换为日期类型，可选择的通过pattern指定转换模板.如果pattern没有指定，缺省使用模板：yyyyMMddHHmmss

### to_char

将一个日期型转化为一个字符串。

语法：
```
	to_char(date_expr[,pattern])
```
将表达式date_expr转化为字符串，可选择的通过pattern指定转换模板.如果pattern没有指定，缺省使用模板：yyyyMMddHHmmss

### to_long

将一个字符串转换为整形数值。

语法：
```
	to_long(string_expr)
```
将表达式string_expr转换为long值。

### to_double

将一个字符串转换为double数值。

语法：
```
	to_double(string_expr)
```
将表达式string_expr转换为double值。

### to_string

将任意类型转换为字符串。

语法：
```
	to_string(expr)
```
将表达式expr转换为字符串。

### strlen

计算字符串长度。

语法：
```
	strlen(str_expr)
```
计算指定的str_expr的字符串长度。

### substr

从指定字符串中获取子字符串。

语法：
```
	substr(src_str,start_offset,length)
```
获取src_str的子字符串，从start_offset起始，长度为length.

### instr

在字符串中查找指定字符串的匹配位置。

语法：
```
	instr(src_str,child_str)
```
在src_str中查找child_str,如果匹配，返回起始的索引号，反之返回-1

### match
测试指定的字符串是否匹配指定的正则表达式。

语法：
```
	match(src_str,regex)
```
测试src_str是否和正则表达式匹配。