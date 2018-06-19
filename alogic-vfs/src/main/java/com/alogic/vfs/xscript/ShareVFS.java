package com.alogic.vfs.xscript;

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
 * 将vfs下面的资源文件转化为共享url
 * 
 * @author yyduan
 * @since 1.6.4.37
 */
public class ShareVFS extends AbstractLogiclet{
	protected String pid = "$vfs";
	protected String $path = "";
	protected String id = "$vfs-share";
	
	public ShareVFS(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		id = PropertiesConstants.getString(p,"id",id,true);
		$path = PropertiesConstants.getRaw(p,"path",$path);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		VirtualFileSystem vfs = ctx.getObject(pid);
		if (vfs == null){
			throw new BaseException("core.e1001",String.format("Can not find vfs:%s", pid));
		}
		
		String path = PropertiesConstants.transform(ctx, $path, "");
		if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(path)){
			ctx.SetValue(id, vfs.getSharePath(path));
		}
	}
}
