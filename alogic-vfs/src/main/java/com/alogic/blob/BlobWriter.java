package com.alogic.blob;

import java.io.OutputStream;

/**
 * BlobWriter
 * 
 * @author yyduan
 *
 */
public interface BlobWriter {

	/**
	 * 获取输出流
	 * @return 输出流
	 */
	public OutputStream getOutputStream();
	
	/**
	 * 完成写入
	 * @param out 输出流
	 */
	public void finishWrite(OutputStream out);
	
	/**
	 * 获取文件信息
	 * @return BlobInfo
	 */
	public BlobInfo getBlobInfo();
}
