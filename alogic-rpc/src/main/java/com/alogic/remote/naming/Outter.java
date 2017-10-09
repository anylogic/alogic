package com.alogic.remote.naming;

import com.alogic.remote.Client;
import com.alogic.remote.httpclient.HttpClient;
import com.alogic.naming.context.XmlOutter;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 外部的XML context
 * 
 * @author yyduan
 * @since 1.6.10.3
 */
public class Outter extends XmlOutter<Client> {
	protected String dftClass = HttpClient.class.getName();
	
	@Override
	public void configure(Properties p) {
		dftClass = PropertiesConstants.getString(p,"dftClass",dftClass);
	}

	@Override
	public String getObjectName() {
		return "client";
	}

	@Override
	public String getDefaultClass() {
		return dftClass;
	}

	@Override
	public String getDefaultXrc() {
		return "java:///com/alogic/remote/naming/default.xrc.xml#" + Outter.class.getName();
	}
}
