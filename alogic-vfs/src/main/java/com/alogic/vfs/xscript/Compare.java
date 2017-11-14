package com.alogic.vfs.xscript;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.vfs.client.Directory;
import com.alogic.vfs.client.Tool;
import com.alogic.vfs.client.ToolImpl2;
import com.alogic.vfs.core.VirtualFileSystem;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;

/**
 * 目录比较
 * @author yyduan
 * @version 1.6.7.11 [20170203 duanyy] <br>
 * - 增加toString实现 <br>
 * 
 * @version 1.6.9.1 [20170516 duanyy] <br>
 * - 修复部分插件由于使用新的文档模型产生的兼容性问题 <br>
 * 
 * @version 1.6.10.6 [20171114 duanyy] <br>
 * - 比较和同步增加路径的白名单和黑名单功能 <br>
 */
public class Compare extends AbstractLogiclet {
	protected String srcId = "$vfs-src";
	protected String destId = "$vfs-dest";
	protected String reportId = "$vfs-report";
	protected String srcPath = "/";
	protected String destPath = "/";
	protected String blacklist = "";
	protected String whitelist = "";
	
	public Compare(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		srcId = PropertiesConstants.getString(p,"src",srcId,true);
		destId = PropertiesConstants.getString(p,"dest",destId,true);
		reportId = PropertiesConstants.getString(p,"report",reportId,true);
		srcPath = PropertiesConstants.getRaw(p,"srcPath",srcPath);
		destPath = PropertiesConstants.getRaw(p,"destPath",destPath);
		blacklist = PropertiesConstants.getRaw(p,"blacklist",blacklist);
		whitelist = PropertiesConstants.getRaw(p,"whitelist",whitelist);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String srcPathValue = ctx.transform(srcPath);
		String destPathValue = ctx.transform(destPath);
		
		Tool.Watcher report = ctx.getObject(reportId);
		
		Tool tool = new ToolImpl2();
		
		tool.setBlacklist(ctx.transform(blacklist));
		tool.setWhitelist(ctx.transform(whitelist));	
		
		String [] paths = srcPathValue.split(";");
		for (String path:paths){
			String vfsId = getVFSId(path,srcId);
			String vfsPath = getVFSPath(path,"/");
			
			VirtualFileSystem src = ctx.getObject(vfsId);
			if (src == null){
				throw new BaseException("core.no_vfs_context",String.format("Can not find vfs:%s", vfsId));
			}
			
			tool.addSource(new Dir(vfsPath,src));
		}
		
		String destVFSId = getVFSId(destPathValue,destId);
		String destVFSPath = getVFSPath(destPathValue,"/");
		
		VirtualFileSystem dest = ctx.getObject(destVFSId);
		if (dest == null){
			throw new BaseException("core.no_vfs_context",String.format("Can not find vfs:%s", destVFSId));
		}
		
		tool.setDestination(new Dir(destVFSPath,dest));
		
		tool.compare(report);
	}

	protected String getVFSId(String path,String dft){
		String [] vals = path.split(":");
		if (vals.length == 1){
			return dft;
		}
		
		return StringUtils.isEmpty(vals[0])?dft:vals[0];
	}

	protected String getVFSPath(String path,String dft){
		String [] vals = path.split(":");
		if (vals.length == 1){
			return StringUtils.isEmpty(vals[0])?dft:vals[0];
		}
		
		return StringUtils.isEmpty(vals[1])?dft:vals[1];
	}
	
	/**
	 * 目录
	 * @author yyduan
	 *
	 */
	public static class Dir implements Directory{
		protected String path = "/";
		protected VirtualFileSystem vfs = null;
		
		public Dir(String p,VirtualFileSystem fs){
			path = p;
			vfs = fs;
		}
		
		@Override
		public void configure(Properties p) {
			// nothing to do
		}

		@Override
		public void configure(Element e, Properties p) {
			XmlElementProperties props = new XmlElementProperties(e,p);
			configure(props);
		}

		@Override
		public VirtualFileSystem getFileSystem() {
			return vfs;
		}

		@Override
		public String getPath() {
			return path;
		}
		
		@Override
		public String toString(){
			return String.format("%s-%s", vfs,path);
		}
	}
}
