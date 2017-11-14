package com.alogic.ac.loader.av;

import com.alogic.ac.AccessVerifier;
import com.alogic.ac.verifier.None;
import com.alogic.load.Loader;

/**
 * 基于XmlResource的AccessVerifier Loader
 * @author yyduan
 * @since 1.6.10.6
 */
public class AVXmlResource extends Loader.XmlResource<AccessVerifier>{

	@Override
	protected String getObjectXmlTag() {
		return "model";
	}

	@Override
	protected String getObjectDftClass() {
		return None.class.getName();
	}

}
