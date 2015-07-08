package com.alogic.blob.context;

import com.alogic.blob.core.BlobManager;
import com.alogic.blob.local.LocalBlobManager;
import com.anysoft.context.Inner;

/**
 * XML内置配置实现
 * 
 * @author duanyy
 * @since 1.6.3.28
 */
public class XmlInner extends Inner<BlobManager> {

	@Override
	public String getObjectName() {
		return "blob";
	}

	@Override
	public String getDefaultClass() {
		return LocalBlobManager.class.getName();
	}

}
