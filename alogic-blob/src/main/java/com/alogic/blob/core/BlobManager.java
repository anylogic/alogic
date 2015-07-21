package com.alogic.blob.core;

import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * Blob管理器
 * 
 * @author duanyy
 * @since 1.6.3.28
 * @version 1.6.3.32 [duanyy 20150720] <br>
 * - 增加md5,content-type等信息 <br>
 */
public interface BlobManager extends XMLConfigurable,Reportable{
	
	/**
	 * 新建Blob文件
	 * @param contentType 文件的content-type
	 * @return BlobWriter实例
	 * 
	 */
	public BlobWriter newFile(String contentType);
	
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
	 * 提交文件
	 * @param writer BlobWriter
	 */
	public void commit(BlobWriter writer);
	
	/**
	 * 取消文件注册
	 * @param writer BlobWriter
	 */
	public void cancel(BlobWriter writer);
}
