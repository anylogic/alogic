package com.logicbus.remote.client;

/**
 * 客户端异常类
 * 
 * @author duanyy
 * @since 1.0.4
 */
public class ClientException extends Exception{

	private static final long serialVersionUID = 1L;

	/**
	 * 错误代码
	 */
	private String m_code;
	
	/**
	 * 获取错误代码
	 * @return
	 */
	public String getCode(){return m_code;}

	/**
	 * constructor
	 * 
	 * @param code 错误代码
	 * @param message 错误原因
	 */
	public ClientException(String code,String message){
		super(message);
		m_code = code;		
	}
}
