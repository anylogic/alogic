package com.logicbus.backend;

import com.anysoft.util.BaseException;


/**
 * 服务过程异常类
 * 
 * @author duanyy
 * @version 1.2.4 [20140703 duanyy]
 * - 修改父类为BaseException
 */
public class ServantException extends BaseException {
	private static final long serialVersionUID = -5968077876441355718L;

	/**
	 * constructor
	 * 
	 * @param code 错误代码
	 * @param message 错误原因
	 */
	public ServantException(String code,String message){
		super(code,message);	
	}
}
