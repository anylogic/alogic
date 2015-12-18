package com.alogic.blob.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.alogic.blob.core.BlobInfo;
import com.alogic.blob.core.BlobWriter;
import com.anysoft.util.IOTools;

/**
 * 本地实现的BlobWriter
 * 
 * @author duanyy
 * @since 1.6.3.32
 * 
 * @version 1.6.4.2 <br>
 * - BlobInfo增加length <br>
 * 
 * @version 1.6.4.18 [duanyy 20151218] <br>
 * - 增加自动图标集 <br>
 */ 
public class LocalBlobWriter implements BlobWriter{
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(BlobWriter.class);	
	
	/**
	 * BlobInfo
	 */
	protected BlobInfo.Default info;
	
	/**
	 * Real file
	 */
	protected File file;
	
	protected String id;
	
	public LocalBlobWriter(String pId,File pFile,String contentType){
		id = pId;
		file = pFile;
		info = new BlobInfo.Default(id,contentType);
	}
	
	@Override
	public OutputStream getOutputStream() {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			logger.error("Can not find file:" + file.getPath(),e);
		}
		
		return out;
	}

	@Override
	public BlobInfo getBlobInfo() {
		String md5 = info.md5();
		if (md5 == null || md5.length() <= 0){
			FileInputStream in = null;
			try {
				in = new FileInputStream(file);
				info.md5(DigestUtils.md5Hex(in));
				info.length(file.length());
			} catch (FileNotFoundException e) {
				logger.error("Can not find file:" + file.getPath(),e);
			} catch (IOException e) {
				logger.error("Can not read file:" + file.getPath(),e);
			}finally{
				IOTools.close(in);
			}
		}
		
		return info;
	}
}
