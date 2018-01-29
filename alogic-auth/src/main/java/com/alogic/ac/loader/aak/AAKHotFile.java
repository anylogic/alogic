package com.alogic.ac.loader.aak;

import com.alogic.ac.AccessAppKey;
import com.alogic.load.Loader;

/**
 * 热部署文件
 * 
 * @author yyduan
 * 
 * @version 1.6.11.14 [duanyy 20180129] <br>
 * - 变更为从Loader.HotFile继承; <br>
 * 
 */
public class AAKHotFile extends Loader.HotFile<AccessAppKey>{

	@Override
	protected String getObjectXmlTag() {
		return "model";
	}

	@Override
	protected String getObjectDftClass() {
		return AccessAppKey.Default.class.getName();
	}

}
