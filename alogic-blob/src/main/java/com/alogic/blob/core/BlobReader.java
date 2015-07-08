package com.alogic.blob.core;

import java.io.InputStream;

/**
 * BlobReader
 * 
 * @author duanyy
 * @since 1.6.3.28
 */
public interface BlobReader extends BlobInfo{
	
	/**
	 * 获取输入流
	 * @param offset 读入的起始位置
	 * @return 输入流
	 */
	public InputStream getInputStream(long offset);
}
