package com.alogic.cas.client.loader;

import com.alogic.cas.client.CasServer;
import com.alogic.cas.client.DefaultCasServer;
import com.alogic.load.Loader;

/**
 * XML内部节点
 * @author yyduan
 * @since 1.6.11.60 [20180912 duanyy]
 */
public class FromInner extends Loader.XmlResource<CasServer>{

	@Override
	protected String getObjectDftClass() {
		return DefaultCasServer.class.getName();
	}

	@Override
	protected String getObjectXmlTag() {
		return "server";
	}
}
