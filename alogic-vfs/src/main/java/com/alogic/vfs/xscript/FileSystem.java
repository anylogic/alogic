package com.alogic.vfs.xscript;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.vfs.context.FileSystemSource;
import com.alogic.vfs.core.VirtualFileSystem;
import com.alogic.vfs.local.LocalFileSystem;
import com.alogic.vfs.sftp.SFtp;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Factory;
import com.anysoft.util.XmlTools;

/**
 * 打开一个VFS文件系统
 * 
 * @author yyduan
 *
 */
public class FileSystem extends VFS{
	/**
	 * 全局VFS的ID
	 */
	protected String globalId = "";
	
	/**
	 * VFS
	 */
	protected VirtualFileSystem filesystem = null;
	
	protected String cid = "$vfs";
	
	public FileSystem(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Element element, Properties props) {
		super.configure(element, props);
		
		if (StringUtils.isEmpty(globalId)){
			//如果没有定义全局的VFS,取本地配置
			VirtualFileSystemFactory f = new VirtualFileSystemFactory();
			try{
				filesystem = f.newInstance(element, props);
			}catch (Exception ex){
				log(String.format("Can not create file system with %s",XmlTools.node2String(element)));
			}
		}else{
			filesystem = FileSystemSource.get().get(globalId);
		}		
	}		
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		globalId = PropertiesConstants.getString(p,"globalId",globalId,true);
		cid = getCurrentId(p);
	}
	
	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(cid) && filesystem != null){
			try{
				ctx.setObject(cid, filesystem);
				super.onExecute(root, current, ctx, watcher);
			}finally{
				ctx.removeObject(cid);
			}
		}
	}
	
	/**
	 * 获取当前对象id
	 * @param p 
	 * @return
	 */
	protected String getCurrentId(Properties p) {
		return PropertiesConstants.getString(p,"cid",cid,true);
	}

	/**
	 * 工厂类
	 * @author yyduan
	 *
	 */
	public static class VirtualFileSystemFactory extends Factory<VirtualFileSystem>{
		protected static Map<String,String> mapping = new HashMap<String,String>();
		public String getClassName(String module){
			String found = mapping.get(module);
			return StringUtils.isEmpty(found)?module:found;
		}
		
		static{
			mapping.put("local", LocalFileSystem.class.getName());
			mapping.put("sftp", SFtp.class.getName());
		}
	}

	/**
	 * 源文件系统
	 * 
	 * @author yyduan
	 *
	 */
	public static class Source extends FileSystem{

		public Source(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		protected String getCurrentId(Properties p) {
			return PropertiesConstants.getString(p,"cid","$vfs-src",true);
		}
	}
	
	/**
	 * 目的文件系统
	 * @author yyduan
	 *
	 */
	public static class Destination extends FileSystem{

		public Destination(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		protected String getCurrentId(Properties p) {
			return PropertiesConstants.getString(p,"cid","$vfs-dest",true);
		}
	}
}
