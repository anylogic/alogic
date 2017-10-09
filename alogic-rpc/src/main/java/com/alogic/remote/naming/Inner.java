package com.alogic.remote.naming;

import com.alogic.remote.Client;
import com.alogic.remote.httpclient.HttpClient;
import com.alogic.naming.context.XmlInner;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * XML内部context
 * 
 * @since 1.6.10.3
 */
public class Inner extends XmlInner<Client> {

	protected String dftClass = HttpClient.class.getName();
	
	@Override
	public String getObjectName() {
		return "client";
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
