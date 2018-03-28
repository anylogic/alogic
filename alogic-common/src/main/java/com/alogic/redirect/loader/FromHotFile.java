package com.alogic.redirect.loader;

import com.alogic.load.Loader;
import com.alogic.redirect.RedirectPath;

/**
 * 从热部署文件中装入
 * 
 * @author yyduan
 * @since 1.6.11.26
 */
public class FromHotFile extends Loader.HotFile<RedirectPath>{

	@Override
	protected String getObjectDftClass() {
		return RedirectPath.Default.class.getName();
	}
	
	protected String getObjectXmlTag() {
		return "model";
	}

}
