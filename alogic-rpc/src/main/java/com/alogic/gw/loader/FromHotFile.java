package com.alogic.gw.loader;

import com.alogic.gw.OpenServiceDescription;
import com.alogic.load.Loader;

/**
 * 从热部署文件中装入
 * 
 * @author yyduan
 * @since 1.6.11.4
 */
public class FromHotFile extends Loader.HotFile<OpenServiceDescription>{

	@Override
	protected String getObjectDftClass() {
		return OpenServiceDescription.Default.class.getName();
	}
	
	protected String getObjectXmlTag() {
		return "model";
	}

}
