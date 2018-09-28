package com.alogic.oauth.client.loader;

import com.alogic.load.Loader;
import com.alogic.oauth.client.DefaultOAuthServer;
import com.alogic.oauth.client.OAuthServer;

/**
 * XML内部节点
 * @author yyduan
 * @since 1.6.11.61
 */
public class FromInner extends Loader.XmlResource<OAuthServer>{

	@Override
	protected String getObjectDftClass() {
		return DefaultOAuthServer.class.getName();
	}

	@Override
	protected String getObjectXmlTag() {
		return "server";
	}
}
