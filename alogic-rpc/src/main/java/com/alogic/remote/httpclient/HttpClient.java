package com.alogic.remote.httpclient;

import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.remote.AbstractClient;
import com.alogic.remote.Client;
import com.alogic.remote.Request;
import com.alogic.remote.backend.Backend;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlTools;

/**
 * 基于apache http client实现的Client
 * 
 * @author yyduan
 *
 */
public class HttpClient extends AbstractClient{

	@Override
	public void configure(Properties p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configure(Element e, Properties p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Request build(String method) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Client addBackend(String appId,Backend backend) {
		// TODO Auto-generated method stub
		return null;
	}



}
