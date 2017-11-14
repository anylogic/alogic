package com.alogic.ac.loader.acm;

import com.alogic.ac.AccessControlModel;
import com.alogic.load.Loader;

/**
 * 基于XmlResource的ACM Loader
 * 
 * @author yyduan
 * @since 1.6.10.6
 */
public class ACMXmlResource extends Loader.XmlResource<AccessControlModel>{

	@Override
	protected String getObjectXmlTag() {
		return "model";
	}

	@Override
	protected String getObjectDftClass() {
		return AccessControlModel.Default.class.getName();
	}

}

