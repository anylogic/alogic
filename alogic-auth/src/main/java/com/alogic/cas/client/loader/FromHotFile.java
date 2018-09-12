package com.alogic.cas.client.loader;

import com.alogic.cas.client.CasServer;
import com.alogic.cas.client.DefaultCasServer;
import com.alogic.load.Loader;

/**
 * 从热部署文件中装入
 * 
 * @author yyduan
 * @since 1.6.11.60 [20180912 duanyy]
 */
public class FromHotFile extends Loader.HotFile<CasServer>{

	@Override
	protected String getObjectDftClass() {
		return DefaultCasServer.class.getName();
	}
	
	protected String getObjectXmlTag() {
		return "server";
	}

}
