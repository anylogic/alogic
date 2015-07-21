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
	public LocalBlobReader(String _id,File _file,BlobInfo _info){
		id = _id;
		info = _info;
		file = _file;
	}
	
	public InputStream getInputStream(long offset) {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			if (offset > 0){
				in.skip(offset);
			}
		} catch (FileNotFoundException e) {
			logger.error("Can not find file:" + file.getPath());
		} catch (IOException e) {
			logger.error("Skip is not supported");
		}
		
		return in;
	}
	public BlobInfo getBlobInfo() {
		return info;
	}
}
