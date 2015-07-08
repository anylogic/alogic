package com.alogic.blob.core;

import java.io.OutputStream;

/**
 * BlobWriter
 * @author duanyy
 * @since 1.6.3.28
 */
public interface BlobWriter extends BlobInfo{
	
	/**
	 * 获取输出流
	 * @return 输出流
	 */
	public OutputStream getOutputStream();
}
