package com.alogic.blob.resource;

import com.alogic.blob.core.BlobInfo;

/**
 * 基于Resource的BlobInfo
 * 
 * @author duanyy
 * @since 1.6.4.7
 * 
 * @version 1.6.4.18 [duanyy 20151218] <br>
 * - 增加自动图标集 <br>
 */
public class ResourceBlobInfo extends BlobInfo.Default{
	protected String path;

	public ResourceBlobInfo(String pId, String pContentType, String pMd5,
			long pLength,String pPath) {
		super(pId, pContentType, pMd5, pLength);
		path = pPath;
	}

	public String getPath(){
		return path;
	}
}
