package com.alogic.remote.call;

import com.anysoft.util.BaseException;

/**
 * 远程调用异常
 * 
 * @author duanyy
 * 
 * @since 1.2.9
 * 
 * @version 1.2.9.3 [20141021 duanyy]
 * - add {@link #CallException(String, String)}
 * 
 * @version 1.6.8.13 [duanyy 20170427] <br>
 * - 从alogic-remote中迁移过来 <br>
 */
public class CallException extends BaseException{

	public CallException(String _code, String _message, Exception _source) {
		super(_code, _message, _source);
	}

	public CallException(String _code, String _msg) {
		super(_code,_msg);
	}

	private static final long serialVersionUID = -253296063636021828L;

}
