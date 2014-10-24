package com.logicbus.remote.context;

import com.anysoft.context.Context;
import com.anysoft.context.Inner;
import com.logicbus.remote.core.Call;
import com.logicbus.remote.impl.http.HttpCall;

/**
 * Source配置文件内联的Context实现
 * 
 * @author duanyy
 *
 * @since 1.2.9
 */
public class InnerContext extends Inner<Context<Call>> {

	
	public String getObjectName() {
		return "call";
	}

	
	public String getDefaultClass() {
		return HttpCall.class.getName();
	}
}
