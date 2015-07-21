package com.alogic.blob.core;

import java.io.InputStream;

/**
 * BlobReader
 * 
 * @author duanyy
 * @since 1.6.3.28
 * 
 * @version 1.6.3.32 [duanyy 20150720] <br>
 * - 增加md5,content-type等信息 <br>
 */
public interface BlobReader{
	
	/**
	 * 获取输入流
	 * @param offset 读入的起始位置
	 * @return 输入流
	 */
	public InputStream getInputStream(long offset);
	
	/**
	 * 获取BlobInfo
	 * @return BlobInfo
	 */
	public BlobInfo getBlobInfo();
}
