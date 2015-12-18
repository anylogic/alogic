package com.alogic.blob.client;

import com.alogic.blob.context.BlobManagerSource;
import com.alogic.blob.core.BlobManager;

/**
 * Blob工具
 * @author duanyy
 *
 * @version 1.6.4.18 [duanyy 20151218] <br>
 * - 增加自动图标集 <br>
 */
public class BlobTool {
	private BlobTool(){
		
	}
	
	/**
	 * 获取Blob管理器
	 * @param id 管理器id
	 * @return BlobManager
	 */
	public static BlobManager getBlobManager(String id){
		BlobManagerSource source = BlobManagerSource.get();
		return source.get(id);
	}
	
	/**
	 * 获取缺省的Blob管理器
	 * @return BlobManager
	 */
	public static BlobManager getBlobManager(){
		return getBlobManager("default");
	}
}
