package com.alogic.blob.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.alogic.blob.core.BlobInfo;
import com.alogic.blob.core.BlobReader;

/**
 * LocalBlobReader
 * 
 * @author duanyy
 * @since 1.6.3.32
 * 
 * @version 1.6.4.18 [duanyy 20151218] <br>
 * - 增加自动图标集 <br>
 */
public class LocalBlobReader implements BlobReader{
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(BlobReader.class);
	
	/**
	 * 注册器中的文件信息
	 */
	protected BlobInfo info = null;
	
	/**
	 * 实际文件
	 */
	protected File file = null;
	
	protected String id;
	
	public LocalBlobReader(String pId,File pFile,BlobInfo pInfo){
		id = pId;
		info = pInfo;
		file = pFile;
	}
	
	@Override
	public InputStream getInputStream(long offset) {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			if (offset > 0){
				in.skip(offset); // NOSONAR
			}
		} catch (FileNotFoundException e) {
			logger.error("Can not find file:" + file.getPath(),e);
		} catch (IOException e) {
			logger.error("Skip is not supported",e);
		}
		
		return in;
	}
	
	@Override
	public BlobInfo getBlobInfo() {
		return info;
	}
}
