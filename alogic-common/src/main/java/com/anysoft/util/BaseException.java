package com.anysoft.util;

/**
 * 基础异常类
 * 
 * @author duanyy
 * 
 */
public class BaseException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 异常代码
	 */
	protected String code;
	
	/**
	 * 构造函数
	 * @param _code 异常代码
	 * @param _message 异常消息
	 */
	public BaseException(String _code,String _message){
		super(_message);
		code = _code;
	}
	
	/**
	 * 构造函数
	 * @param _code 异常代码
	 * @param _message 异常消息
	 * @param _source 来源异常
	 */
	public BaseException(String _code,String _message,Exception _source){
		super(_message + "->" + _source.getMessage());
		code = _code;
	}
	
	/**
	 * 获取异常代码
	 * @return
	 */
	public String getCode(){return code;}
}
