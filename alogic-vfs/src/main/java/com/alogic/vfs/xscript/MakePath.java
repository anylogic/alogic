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
 * 创建指定的路径
 * @author yyduan
 * @since 1.6.8.5
 * 
 * @version 1.6.9.1 [20170516 duanyy] <br>
 * - 修复部分插件由于使用新的文档模型产生的兼容性问题 <br>
 */
public class MakePath extends AbstractLogiclet{
	protected String pid = "$vfs";
	protected String path = "";
	protected String id = "$vfs-mkdir";
	
	public MakePath(String tag, Logiclet p) {
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
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		VirtualFileSystem vfs = ctx.getObject(pid);
		if (vfs == null){
			throw new BaseException("core.e1001",String.format("Can not find vfs:%s", pid));
		}
		
		String pathValue = ctx.transform(path);
		if (StringUtils.isNotEmpty(pathValue)){
			ctx.SetValue(id, BooleanUtils.toStringTrueFalse(vfs.makeDirs(pathValue)));
		}else{
			ctx.SetValue(id, BooleanUtils.toStringTrueFalse(false));
		}
	}
}