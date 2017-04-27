package com.alogic.rpc.naming;

import com.alogic.naming.context.XmlOutter;
import com.alogic.rpc.Call;
import com.alogic.rpc.call.http.RemoteCall;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * XML外部Context
 * 
 * @author yyduan
 * @since 1.6.7.15
 */
public class Outter extends XmlOutter<Call> {
	protected String dftClass = RemoteCall.class.getName();
	
	@Override
	public void configure(Properties p) {
		dftClass = PropertiesConstants.getString(p,"dftClass",dftClass);
	}

	@Override
	public String getObjectName() {
		return "call";
	}

	@Override
	public String getDefaultClass() {
		return dftClass;
	}

	@Override
	public String getDefaultXrc() {
		return "java:///com/alogic/rpc/context/xrc.default.xml#" + Outter.class.getName();
	}
}
