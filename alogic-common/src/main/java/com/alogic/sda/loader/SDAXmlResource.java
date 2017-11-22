package com.alogic.sda.loader;

import com.alogic.sda.SecretDataArea;
import com.alogic.load.Loader;
import com.alogic.metrics.stream.handler.Default;

/**
 * 基于XMLResource模式实现的SDALoader
 * @author yyduan
 * @since 1.6.10.8
 */
public class SDAXmlResource extends Loader.XmlResource<SecretDataArea>{

	@Override
	protected String getObjectXmlTag() {
		return "model";
	}

	@Override
	protected String getObjectDftClass() {
		return Default.class.getName();
	}

}
