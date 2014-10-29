package com.logicbus.remote.context;

import com.anysoft.context.XMLResource;
import com.logicbus.remote.impl.http.HttpCall;
import com.logicbus.remote.core.Call;

/**
 * 基于外部XRC的Context实现
 * 
 * @author duanyy
 *
 * @since 1.2.9
 */
public class XRC extends XMLResource<Call> {

	
	public String getObjectName() {
		return "call";
	}

	
	public String getDefaultClass() {
		return HttpCall.class.getName();
	}

	
	public String getDefaultXrc() {
		return "java:///com/logicbus/remote/context/call.xrc.default.xml#com.logicbus.remote.context.XRC";
	}

}
