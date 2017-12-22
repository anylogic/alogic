package com.alogic.gw.loader;

import com.alogic.gw.OpenServiceDescription;
import com.alogic.load.Loader;

/**
 * XML内部节点
 * @author yyduan
 * @since 1.6.11.4
 */
public class FromInner extends Loader.XmlResource<OpenServiceDescription>{

	@Override
	protected String getObjectDftClass() {
		return OpenServiceDescription.Default.class.getName();
	}

	@Override
	protected String getObjectXmlTag() {
		return "service";
	}
}
