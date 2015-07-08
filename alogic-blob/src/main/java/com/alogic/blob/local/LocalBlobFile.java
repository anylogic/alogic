package com.alogic.blob.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.alogic.blob.core.BlobReader;
import com.alogic.blob.core.BlobWriter;

/**
 * Blob的本地文件实现
 * 
 * @author duanyy
 * @since 1.6.3.28
 */
public class LocalBlobFile implements BlobReader,BlobWriter {
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(BlobReader.class);
	
	/**
	 * 实际文件
	 */
	protected File file;
	/**
	 * 文件id
	 */
	protected String id;
	
	public LocalBlobFile(String _id,File _file){
		id = _id;
		file = _file;
	}
	
	public String id() {
		return id;
	}

	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("id", id);
			
			if (file != null){
				xml.setAttribute("realPath", file.getPath());
				xml.setAttribute("length", String.valueOf(file.length()));
				xml.setAttribute("lastModified", String.valueOf(file.lastModified()));
			}
		}
	}

	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("id", id);
			
			if (file != null){
				json.put("realPath", file.getPath());
				json.put("length", file.length());
				json.put("lastModified", file.lastModified());
			}
		}
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

	public OutputStream getOutputStream() {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			logger.error("Can not find file:" + file.getPath());
		}
		
		return out;
	}

}
