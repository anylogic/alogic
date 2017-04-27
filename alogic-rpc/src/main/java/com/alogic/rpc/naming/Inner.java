package com.alogic.rpc.naming;

import com.alogic.naming.context.XmlInner;
import com.alogic.rpc.Call;
import com.alogic.rpc.call.http.RemoteCall;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * XML内部context
 * 
 * @author yyduan
 * @since 1.6.7.15
 */
public class Inner extends XmlInner<Call> {

	protected String dftClass = RemoteCall.class.getName();
	
	@Override
	public String getObjectName() {
		return "call";
	}

	@Override
	public String getDefaultClass() {
		return dftClass;
	}

	@Override
	public void configure(Properties p) {
		dftClass = PropertiesConstants.getString(p,"dftClass",dftClass);
	}

}
