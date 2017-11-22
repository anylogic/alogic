package com.alogic.ac.loader.acm;

import com.alogic.ac.AccessControlModel;
import com.alogic.load.Loader;

/**
 * 热部署文件
 * 
 * @author yyduan
 *
 */
public class ACMHotFile extends Loader.HotFile<AccessControlModel>{

	@Override
	protected String getObjectXmlTag() {
		return "model";
	}

	@Override
	protected String getObjectDftClass() {
		return AccessControlModel.Default.class.getName();
	}

}

