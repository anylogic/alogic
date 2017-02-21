package com.alogic.rpc;

import com.anysoft.util.BaseException;

/**
 * 服务调用异常
 * 
 * @author duanyy
 * 
 * @since 1.6.7.15
 * 
 */
public class CallException extends BaseException {

	private static final long serialVersionUID = 1L;

	public CallException(String code,String msg){
		super(code,msg);
	}
	
	public CallException(String code, String msg, Exception src) {
		super(code, msg, src);
	}

}
