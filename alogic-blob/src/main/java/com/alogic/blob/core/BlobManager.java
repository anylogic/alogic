package com.alogic.blob.core;

import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * Blob管理器
 * 
 * @author duanyy
 * @since 1.6.3.28
 */
public interface BlobManager extends XMLConfigurable,Reportable{
	
	/**
	 * 新建Blob文件
	 * 
	 * @return BlobWriter实例
	 * 
	 */
	public BlobWriter newFile();
	
	/**
	 * 查找已存在的Blob文件
	 * 
	 * @param id 文件ID
	 * @return BlobReader实例
	 */
	public BlobReader getFile(String id);
	
	/**
	 * Blob文件是否存在
	 * @param id 文件ID
	 * @return true|false
	 */
	public boolean existFile(String id);
	
	/**
	 * 删除文件
	 * @param id 文件id
	 */
	public boolean deleteFile(String id);
	
	/**
	 * 获取ContentType
	 * @return contentType
	 */
	public String getContentType();
}
