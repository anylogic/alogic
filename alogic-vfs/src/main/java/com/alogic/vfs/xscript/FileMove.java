package com.alogic.vfs.xscript;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.alogic.vfs.core.VirtualFileSystem;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 在vfs中移动文件
 * 
 * @author yyduan
 *
 * @since 1.6.9.6
 */
public class FileMove extends AbstractLogiclet{
	protected String pid = "$vfs";
	protected String src = "";
	protected String dest = "";
	protected boolean overwrite = true;
	protected String id = "$vfs-mv";
	
	public FileMove(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		src = PropertiesConstants.getRaw(p,"src",src);
		dest = PropertiesConstants.getRaw(p,"dest",dest);
		overwrite = PropertiesConstants.getBoolean(p,"overwrite",overwrite);
		id = PropertiesConstants.getString(p,"id",id,true);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		VirtualFileSystem vfs = ctx.getObject(pid);
		if (vfs == null){
			throw new BaseException("core.e1001",String.format("Can not find vfs:%s", pid));
		}
		
		String srcPath = ctx.transform(src);
		String destPath = ctx.transform(dest);
		
		if (StringUtils.isNotEmpty(srcPath) && StringUtils.isNotEmpty(destPath)){
			ctx.SetValue(id,BooleanUtils.toStringTrueFalse(vfs.move(srcPath, destPath, overwrite)));
		}else{
			ctx.SetValue(id,BooleanUtils.toStringTrueFalse(false));
		}
	}
}