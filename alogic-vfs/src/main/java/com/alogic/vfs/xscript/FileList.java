package com.alogic.vfs.xscript;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.vfs.core.VirtualFileSystem;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 查询当前目录的文件列表
 * 
 * @author yyduan
 *
 */
public class FileList extends AbstractLogiclet{
	protected String pid = "$vfs";
	protected String path = "/";
	protected String pattern = "*";
	protected String offset = "0";
	protected String limit = "100";
	protected String tag = "data";
	
	public FileList(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		path = PropertiesConstants.getRaw(p,"path",path);
		pattern = PropertiesConstants.getRaw(p, "pattern", pattern);
		offset = PropertiesConstants.getRaw(p, "offset", offset);
		limit = PropertiesConstants.getRaw(p, "limit", limit);
		tag = PropertiesConstants.getRaw(p, "tag", tag);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		VirtualFileSystem vfs = ctx.getObject(pid);
		if (vfs == null){
			throw new BaseException("core.no_vfs_context",String.format("Can not find vfs:%s", pid));
		}
		
		String tagValue = ctx.transform(tag);
		String pathValue = ctx.transform(path);
		String patternValue = ctx.transform(pattern);
		int offsetValue = getInt(ctx,offset);
		int limitValue = getInt(ctx,limit);
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		vfs.listFiles(pathValue, patternValue, result, offsetValue, limitValue);
		
		if (StringUtils.isNotEmpty(tagValue)){
			current.put(tagValue, result);
		}
	}

	protected int getInt(Properties p,String valuePattern){
		String value = p.transform(valuePattern);
		try{
			return Integer.parseInt(value);
		}catch (NumberFormatException ex){
			return 0;
		}
	}
}
