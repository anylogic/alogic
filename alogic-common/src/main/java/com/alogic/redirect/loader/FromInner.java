package com.alogic.redirect.loader;

import com.alogic.load.Loader;
import com.alogic.redirect.RedirectPath;

/**
 * XML内部节点
 * @author yyduan
 * @since 1.6.11.26
 */
public class FromInner extends Loader.XmlResource<RedirectPath>{

	@Override
	protected String getObjectDftClass() {
		return RedirectPath.Default.class.getName();
	}

	@Override
	protected String getObjectXmlTag() {
		return "model";
	}
}
