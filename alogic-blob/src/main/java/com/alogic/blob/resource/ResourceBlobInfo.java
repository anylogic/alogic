package com.alogic.blob.resource;

import com.alogic.blob.core.BlobInfo;

/**
 * 基于Resource的BlobInfo
 * 
 * @author duanyy
 * @since 1.6.4.7
 */
public class ResourceBlobInfo extends BlobInfo.Default{
	protected String path;

	public ResourceBlobInfo(String _id, String _contentType, String _md5,
			long _length,String _path) {
		super(_id, _contentType, _md5, _length);
		path = _path;
	}

	public String getPath(){
		return path;
	}
}
