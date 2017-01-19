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
 * 目录比较
 * @author yyduan
 *
 */
public class Compare extends AbstractLogiclet {
	protected String srcId = "$vfs-src";
	protected String destId = "$vfs-dest";
	protected String reportId = "$vfs-report";
	protected String srcPath = "/";
	protected String destPath = "/";
	
	public Compare(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		srcId = PropertiesConstants.getString(p,"src",srcId,true);
		destId = PropertiesConstants.getString(p,"dest",destId,true);
		reportId = PropertiesConstants.getString(p,"report",reportId,true);
		srcPath = PropertiesConstants.getString(p,"srcPath",srcPath,true);
		destPath = PropertiesConstants.getString(p,"destPath",destPath,true);
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
		
		Tool.Watcher report = ctx.getObject(reportId);
		
		Tool tool = new ToolImpl();
		
		tool.setSource(new Dir(srcPath,src));
		tool.setDestination(new Dir(destPath,dest));
		
		tool.compare(report);
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
		
	}
}
