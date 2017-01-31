package com.alogic.vfs.xscript;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;

import com.alogic.vfs.core.VirtualFileSystem;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 删除指定的文件
 * 
 * @author duanyy
 * @since 1.6.7.8
 */
public class FileDelete extends AbstractLogiclet{
	protected String pid = "$vfs";
	protected String path = "/";
	protected String id = "$vfs-del";
	
	public FileDelete(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		path = PropertiesConstants.getRaw(p,"path",path);
		id = PropertiesConstants.getString(p,"id",id,true);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		VirtualFileSystem vfs = ctx.getObject(pid);
		if (vfs == null){
			throw new BaseException("core.no_vfs_context",String.format("Can not find vfs:%s", pid));
		}
		
		String pathValue = ctx.transform(path);
		ctx.SetValue(id, BooleanUtils.toStringTrueFalse(vfs.deleteFile(pathValue)));
	}


}
