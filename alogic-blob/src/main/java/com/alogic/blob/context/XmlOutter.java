package com.alogic.blob.context;

import com.alogic.blob.core.BlobManager;
import com.alogic.blob.local.LocalBlobManager;
import com.anysoft.context.XMLResource;

/**
 * XML外置配置实现
 * 
 * @author duanyy
 * @since 1.6.3.28
 */
public class XmlOutter extends XMLResource<BlobManager>{

	@Override
	public String getObjectName() {
		return "blob";
	}

	@Override
	public String getDefaultClass() {
		return LocalBlobManager.class.getName();
	}

	@Override
	public String getDefaultXrc() {
		return "java:///com/alogic/blob/context/blob.default.xml#com.alogic.blob.context.XmlOutter";
	}

}
