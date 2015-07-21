package com.alogic.blob.core;

import java.io.OutputStream;

/**
 * BlobWriter
 * @author duanyy
 * @since 1.6.3.28
 * 
 * @version 1.6.3.32 [duanyy 20150720] <br>
 * - 增加md5,content-type等信息 <br>
 */
public interface BlobWriter{
	
	/**
	 * 获取输出流
	 * @return 输出流
	 */
	public OutputStream getOutputStream();
	
	/**
	 * 获取BlobInfo
	 * @return BlobInfo
	 */
	public BlobInfo getBlobInfo();
}
