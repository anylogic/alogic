package com.alogic.ac.loader.aak;

import com.alogic.ac.AccessAppKey;
import com.alogic.load.Loader;

/**
 * 热部署文件
 * 
 * @author yyduan
 *
 */
public class AAKHotFile extends Loader.XmlResource<AccessAppKey>{

	@Override
	protected String getObjectXmlTag() {
		return "model";
	}

	@Override
	protected String getObjectDftClass() {
		return AccessAppKey.Default.class.getName();
	}

}
