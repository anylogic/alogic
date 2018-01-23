package com.alogic.blob;

import java.io.InputStream;

/**
 * BlobReader
 * @author yyduan
 *
 */
public interface BlobReader {
	
	/**
	 * 获取输入流
	 * @param offset 读入的起始位置
	 * @return 输入流
	 */
	public InputStream getInputStream(long offset);
	
	/**
	 * 完成读入操作
	 * @param in 输入流
	 */
	public void finishRead(InputStream in);
	
	/**
	 * 获取文件信息
	 * @return BlobInfo
	 */
	public BlobInfo getBlobInfo();
}
