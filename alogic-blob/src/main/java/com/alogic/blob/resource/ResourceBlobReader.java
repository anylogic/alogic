package com.alogic.blob.resource;

import java.io.InputStream;

import com.alogic.blob.core.BlobInfo;
import com.alogic.blob.core.BlobReader;

/**
 * BlobReader实现
 * 
 * @author duanyy
 * @since 1.6.4.7
 */
public class ResourceBlobReader implements BlobReader {
	protected ResourceBlobInfo info = null;
	protected Class<?> bootstrap = getClass();
	
	public ResourceBlobReader(ResourceBlobInfo _info,Class<?> _bootstrap){
		info = _info;
		bootstrap = _bootstrap;
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
