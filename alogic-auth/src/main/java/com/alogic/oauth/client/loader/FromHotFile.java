package com.alogic.oauth.client.loader;

import com.alogic.load.Loader;
import com.alogic.oauth.client.DefaultOAuthServer;
import com.alogic.oauth.client.OAuthServer;

/**
 * 从热部署文件中装入
 * 
 * @author yyduan
 * @since 1.6.11.61
 */
public class FromHotFile extends Loader.HotFile<OAuthServer>{

	@Override
	protected String getObjectDftClass() {
		return DefaultOAuthServer.class.getName();
	}
	
	protected String getObjectXmlTag() {
		return "server";
	}

}
