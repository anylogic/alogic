package com.alogic.blob.vfs;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.blob.BlobInfo;
import com.alogic.blob.BlobManager;
import com.alogic.blob.BlobReader;
import com.alogic.blob.BlobWriter;
import com.alogic.vfs.context.FileSystemSource;
import com.alogic.vfs.core.VirtualFileSystem;
import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;
import com.anysoft.util.JsonTools;
import com.anysoft.util.KeyGen;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;

/**
 * 基于vfs的blob管理器
 * @author yyduan
 * 
 * @version 1.6.11.22 [duanyy 20180314] <br>
 * - getFile增加文件是否存在的判断<br>
 * 
 * @version 1.6.11.53 [20180817 duanyy] <br>
 * - BlobManager模型变更 <br>
 */
public class VFSBlobManager extends BlobManager.Abstract{
	/**
	 * 相对于vfs根目录的路径
	 */
	protected String home = "";
	
	/**
	 * VFS
	 */
	protected VirtualFileSystem vfs = null;
	
	/**
	 * content-type
	 */
	protected String contentType = "text/plain";
	
	/**
	 * 是否采用多目录存储模式
	 */
	protected boolean multiDir = false;
	
	/**
	 * 获取Content-Type
	 * @return Content-Type
	 */
	public String getContentType(){
		return contentType;
	}
	
	/**
	 * 通过文件id映射实际路径
	 * @param id 文件id
	 * @return 实际路径
	 */
	protected String getRealPath(String id){
		long hash = id.hashCode() & Long.MAX_VALUE;
		
		return multiDir ? home + File.separator 
				+ (hash/100 % 10) + File.separator 
				+ (hash/10 % 10) + File.separator
				+ (hash % 10) : home;
	}
	
	/**
	 * 获取文件的完整路径
	 * @param path 目录
	 * @param id 文件id
	 * @return 完整路径
	 */
	protected String getFullPath(String path,String id){
		return path + File.separator + id;
	}
	
	@Override
	public void report(Element xml) {
		if (xml != null){
			super.report(xml);
			XmlTools.setString(xml, "home", this.home);
			XmlTools.setString(xml, "bootstrap", this.vfs.id());
			XmlTools.setString(xml, "contentType", this.getContentType());
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			super.report(json);
			JsonTools.setString(json, "home", this.home);
			JsonTools.setString(json,"bootstrap",this.vfs.id());
			JsonTools.setString(json, "contentType", this.getContentType());
		}
	}	
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		contentType =  PropertiesConstants.getString(p,"contentType", contentType,true);
		multiDir = PropertiesConstants.getBoolean(p,"multiDir", multiDir,true);
		
		String vfsId = PropertiesConstants.getString(p,"vfsId", "default",true);
		vfs = FileSystemSource.get().get(vfsId);
		
		if (vfs == null){
			throw new BaseException("core.e1003","Can not find vfs:" + vfsId);
		}
		
		home = PropertiesConstants.getString(p,"home", home);
		
		if (!vfs.exist(home)){
			//保证home存在
			vfs.makeDirs(home);
		}
	}
	
	@Override
	public BlobWriter newFile(String id) {
		String fileId = StringUtils.isEmpty(id)?newFileId():id;

		String path = getRealPath(fileId);
		if (!vfs.exist(path)){
			vfs.makeDirs(path);
		}
		
		return new VFSBlobWriter(new VFSBlobInfo(
				fileId, 
				getFullPath(path,fileId), 
				getContentType()
				), 
				vfs);
	}

	protected String newFileId(){
		return String.format("%d%s",System.currentTimeMillis(),KeyGen.uuid(6, 0, 9));
	}
	
	@Override
	public BlobReader getFile(String id) {
		String path = getFullPath(getRealPath(id),id);	
		return vfs.exist(path) ? new VFSBlobReader(new VFSBlobInfo(
				id,
				path,
				getContentType()
				),
				vfs):null;
	}

	@Override
	public boolean existFile(String id) {
		String path = getRealPath(id);
		return vfs.exist(getFullPath(path,id));
	}

	@Override
	public boolean deleteFile(String id) {
		String path = getRealPath(id);
		return vfs.deleteFile(getFullPath(path,id));
	}

	/**
	 * BlobInfo
	 * @author yyduan
	 *
	 */
	public static class VFSBlobInfo extends BlobInfo.Abstract{
		protected String path;
		
		public VFSBlobInfo(String id,String path,String contentType) {
			super(id,contentType);
			this.path = path;
		}
		
		/**
		 * 获取路径
		 * @return 路径
		 */
		public String getPath(){
			return this.path;
		}
	}
	
	/**
	 * Writer
	 * @author yyduan
	 *
	 */
	public static class VFSBlobWriter implements BlobWriter{
		protected VFSBlobInfo info = null;
		protected VirtualFileSystem vfs = null;
		protected int bufferSize = 10 * 1024;
		
		public VFSBlobWriter(VFSBlobInfo info,VirtualFileSystem vfs){
			this.info = info;
			this.vfs = vfs;
		}
		
		public VFSBlobWriter(VFSBlobInfo info,VirtualFileSystem vfs,int bufferSize){
			this.info = info;
			this.vfs = vfs;
			this.bufferSize = bufferSize > 0 ? bufferSize : 10 * 1024;
		}		
		
		@Override
		public BlobInfo getBlobInfo() {
			return info;
		}

		@Override
		public void write(InputStream in, long contentLength,
				boolean toCloseStreamWhenFinished) {
			OutputStream out = vfs.writeFile(info.getPath());
			byte[] buffer = new byte[bufferSize];			
			int size = 0;
			try {
				while ((size = in.read(buffer)) != -1) {
					out.write(buffer, 0, size);
				}
			}catch (Exception ex){
				throw new BaseException("core.e1004", ex.getMessage());
			}finally{
				vfs.finishWrite(info.getPath(), out);
				if (toCloseStreamWhenFinished){
					IOTools.close(in);
				}
			}
		}

		@Override
		public void write(byte[] content) {
			OutputStream out = vfs.writeFile(info.getPath());
			try {
				out.write(content);
			}catch (Exception ex){
				throw new BaseException("core.e1004", ex.getMessage());
			}finally{
				vfs.finishWrite(info.getPath(), out);
			}
		}
		
	}
	
	/**
	 * Reader
	 * @author yyduan
	 *
	 */
	public static class VFSBlobReader implements BlobReader{
		protected VFSBlobInfo info = null;
		protected VirtualFileSystem vfs = null;
		
		public VFSBlobReader(VFSBlobInfo info,VirtualFileSystem vfs){
			this.info = info;
			this.vfs = vfs;
		}
		
		@Override
		public InputStream getInputStream(long offset) {
			return vfs.readFile(info.getPath());
		}

		@Override
		public void finishRead(InputStream in) {
			vfs.finishRead(info.getPath(), in);
		}

		@Override
		public BlobInfo getBlobInfo() {
			return info;
		}
		
	}
}
