package com.alogic.blob.resource;

import java.io.InputStream;

import com.alogic.blob.core.BlobInfo;
import com.alogic.blob.core.BlobReader;

/**
 * BlobReader实现
 * 
 * @author duanyy
 * @since 1.6.4.7
 * 
 * @version 1.6.4.18 [duanyy 20151218] <br>
 * - 增加自动图标集 <br>
 */
public class ResourceBlobReader implements BlobReader {
	protected ResourceBlobInfo info = null;
	protected Class<?> bootstrap = getClass();
	
	public ResourceBlobReader(ResourceBlobInfo pInfo,Class<?> pBootstrap){
		info = pInfo;
		bootstrap = pBootstrap;
	}
	
	@Override
	public InputStream getInputStream(long offset) {
		String path = info.getPath();
		return bootstrap.getResourceAsStream(path);
	}

	@Override
	public BlobInfo getBlobInfo() {
		return info;
	}

}
