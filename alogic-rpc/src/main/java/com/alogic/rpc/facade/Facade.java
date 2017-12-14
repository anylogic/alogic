package com.alogic.rpc.facade;

import com.alogic.rpc.Call;
import com.alogic.rpc.CallException;
import com.alogic.rpc.Parameters;
import com.alogic.rpc.Result;

/**
 * Facade
 * 
 * @author duanyy
 * @since 1.6.7.15
 */
public class Facade {
	/**
	 * 过程调用
	 */
	protected Call call = null;

	public Facade() {
		// nothing to do
	}

	public Facade(Call theCall) {
		call = theCall;
	}

	public void setCall(Call theCall) {
		call = theCall;
	}

	protected Object execute(String id, String method, Object... parameters) throws Throwable {
		Parameters params = call.newParameters();

		params.params(parameters);

		Result ret = call.invoke(id, method, params);

		if (ret == null) {
			throw new CallException("core.e1606", "Remote call is failed.");
		}
		if (!ret.code().equals("core.ok")) {
			throw new CallException(ret.code(), ret.reason());
		}

		Throwable t = ret.getThrowable();
		if (t != null) {
			throw t;
		}

		return ret.ret();
	}
}
