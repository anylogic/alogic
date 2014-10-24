package com.anysoft.formula;

/**
 * helper to customize a function
 * @author duanyy
 * @version 1.0.0
 */
public interface FunctionHelper {
	/**
	 * 生成自定义函数实现的实例
	 * 
	 * <br>函数实现的查找次序为：内置静态的函数映射表->本实例的函数映射表->父节点
	 * 
	 * @param funcName 函数名
	 */	
	public Expression customize(String funcName);
}
