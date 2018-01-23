package com.alogic.vfs.xscript;

import java.util.List;

import org.apache.commons.fileupload.FileItem;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.message.MultiPartForm;

/**
 * 扫描上传文件
 * @author yyduan
 *
 */
public class Upload extends Segment {
	
	/**
	 * 上层对象id
	 */
	protected String pid = "$message";
	
	/**
	 * 当前对象id
	 */
	protected String cid = "$upload";
	
	public Upload(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		cid = PropertiesConstants.getString(p,"cid",cid,true);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		MultiPartForm msg = ctx.getObject(pid);
		if (msg == null){
			throw new BaseException("core.e1001","It must be in a UploadTogetherServant servant,check your together script.");
		}
		
		List<FileItem> fileItems = msg.getFileItems();
		for (FileItem item:fileItems){
			try {
				ctx.setObject(cid, item);
				ctx.SetValue("$upload-content-type", item.getContentType());
				ctx.SetValue("$upload-field", item.getFieldName());
				ctx.SetValue("$upload-name", item.getName());
				ctx.SetValue("$upload-size", String.valueOf(item.getSize()));
				super.onExecute(root, current, ctx, watcher);
			}finally{
				ctx.removeObject(cid);
			}
		}
	}
}
