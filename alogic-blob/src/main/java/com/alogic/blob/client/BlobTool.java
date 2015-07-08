package com.alogic.blob.client;

import com.alogic.blob.context.BlobManagerSource;
import com.alogic.blob.core.BlobManager;

/**
 * Blob工具
 * @author duanyy
 *
 */
public class BlobTool {
	
	/**
	 * 获取Blob管理器
	 * @param id 管理器id
	 * @return BlobManager
	 */
	static public BlobManager getBlobManager(String id){
		BlobManagerSource source = BlobManagerSource.get();
		return source.get(id);
	}
	
	/**
	 * 获取缺省的Blob管理器
	 * @return BlobManager
	 */
	static public BlobManager getBlobManager(){
		return getBlobManager("default");
	}
}
