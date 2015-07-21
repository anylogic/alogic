package com.alogic.blob.core;

import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * 文件注册器
 * 
 * <p>文件注册器用于保存文件的元数据信息，如md5码，content-type等
 * 
 * @author duanyy
 * @since 1.6.3.32 
 */
public interface BlobRegister extends XMLConfigurable,Reportable{
	/**
	 * 查找Blob文件信息
	 * @param id 文件id
	 * @return BlobInfo
	 */
	public BlobInfo find(String id);
	
	/**
	 * 注册Blob文件信息
	 * @param info BlobInfo
	 */
	public void add(BlobInfo info);
	
	/**
	 * 删除文件信息
	 * @param id 文件id
	 */
	public void delete(String id);
}
