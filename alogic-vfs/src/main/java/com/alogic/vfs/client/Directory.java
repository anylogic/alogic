package com.alogic.vfs.client;

import org.w3c.dom.Element;

import com.alogic.vfs.context.FileSystemSource;
import com.alogic.vfs.core.VirtualFileSystem;
import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 目录 
 * @author duanyy
 *
 */
public interface Directory extends Configurable,XMLConfigurable{
	
	/**
	 * 获取目录的文件系统 
	 * @return VFS实例
	 */
	public VirtualFileSystem getFileSystem();
	
	/**
	 * 获取目录的路径
	 * @return 路径
	 */
	public String getPath();
	
	public static class Default implements Directory{
		
		/**
		 * 文件系统的id
		 */
		protected String id = "default";
		
		/**
		 * 在文件系统上的路径
		 */
		protected String path = "/";
		
		public Default(){
			
		}
		
		public Default(String fsId){
			id = fsId;
		}
		
		public Default(String fsId,String fsPath){
			id = fsId;
			path = fsPath;
		}
		
		@Override
		public void configure(Properties p) {
			id = PropertiesConstants.getString(p, "id", PropertiesConstants.getString(p, "fsId", id,true),true);
			path = PropertiesConstants.getString(p,"path",PropertiesConstants.getString(p, "fsPath", id,true),true);
		}

		@Override
		public void configure(Element e, Properties p) {
			XmlElementProperties props = new XmlElementProperties(e,p);
			configure(props);
		}

		@Override
		public VirtualFileSystem getFileSystem() {
			return FileSystemSource.get().get(id);
		}

		@Override
		public String getPath() {
			return path;
		}
		
	}
}
