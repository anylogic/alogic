package com.alogic.vfs.xscript;

import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.vfs.client.Directory;
import com.alogic.vfs.client.Tool;
import com.alogic.vfs.client.ToolImpl;
import com.alogic.vfs.core.VirtualFileSystem;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;

/**
 * 目录同步
 * @author yyduan
 * @version 1.6.7.11 [20170203 duanyy] <br>
 * - 增加toString实现 <br>
 */
public class Sync extends AbstractLogiclet {
	protected String srcId = "$vfs-src";
	protected String destId = "$vfs-dest";
	protected String reportId = "$vfs-report";
	protected String srcPath = "/";
	protected String destPath = "/";
	
	public Sync(String tag, Logiclet p) {
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
	}
	
	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		
		VirtualFileSystem src = ctx.getObject(srcId);
		if (src == null){
			throw new BaseException("core.no_vfs_context",String.format("Can not find vfs:%s", srcId));
		}
		VirtualFileSystem dest = ctx.getObject(destId);
		if (dest == null){
			throw new BaseException("core.no_vfs_context",String.format("Can not find vfs:%s", destId));
		}
		
		String srcPathValue = ctx.transform(srcPath);
		String destPathValue = ctx.transform(destPath);
		
		Tool.Watcher report = ctx.getObject(reportId);
		
		Tool tool = new ToolImpl();
		
		tool.setSource(new Dir(srcPathValue,src));
		tool.setDestination(new Dir(destPathValue,dest));
		
		tool.sync(report);
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