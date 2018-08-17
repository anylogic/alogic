package com.alogic.blob;

import java.io.InputStream;

/**
 * BlobWriter
 * 
 * @author yyduan
 * @version 1.6.11.53 [20180817 duanyy] <br>
 * - 改成通过InputStream拉取数据的模式 <br>
 */
public interface BlobWriter {

	/**
	 * 通过输入流写出文件
	 * @param in 输入流
	 * @param contentLength 数据大小
	 * @param toCloseStreamWhenFinished 当完成读数据之后，是否关闭输入流
	 */
	public void write(InputStream in,long contentLength,boolean toCloseStreamWhenFinished);
	
	/**
	 * 写出byte[]内容
	 * @param content byte[] 内容
	 */
	public void write(byte[] content);
	
	/**
	 * 获取文件信息
	 * @return BlobInfo
	 */
	public BlobInfo getBlobInfo();
}
