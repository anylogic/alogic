package com.alogic.ac.loader.aak;

import com.alogic.ac.AccessAppKey;
import com.alogic.load.Loader;


/**
 * 基于XmlResource的AAK
 * @author yyduan
 * @since 1.6.10.6
 */
public class AAKXmlResource extends Loader.XmlResource<AccessAppKey>{

	@Override
	protected String getObjectXmlTag() {
		return "model";
	}

	@Override
	protected String getObjectDftClass() {
		return AccessAppKey.Default.class.getName();
	}

}
