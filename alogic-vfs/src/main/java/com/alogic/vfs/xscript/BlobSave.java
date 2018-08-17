package com.alogic.vfs.xscript;

import java.io.UnsupportedEncodingException;
import org.apache.commons.lang3.StringUtils;
import com.alogic.blob.BlobManager;
import com.alogic.blob.BlobWriter;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 保存内容到Blob
 * 
 * @author yyduan
 * @since 1.6.11.53 
 */
public class BlobSave extends AbstractLogiclet{
	protected String pid = "$blob";
	protected String $fileId = "";
	protected String $content = "";
	protected String encoding = "utf-8";
	protected boolean overwrite = true;
	
	public BlobSave(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		$fileId = PropertiesConstants.getRaw(p,"fileId",$fileId);
		$content = PropertiesConstants.getRaw(p, "content", $content);
		encoding = PropertiesConstants.getString(p,"encoding",encoding,true);
		overwrite = PropertiesConstants.getBoolean(p,"overwrite",overwrite,true);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		BlobManager bm = ctx.getObject(pid);
		if (bm == null){
			throw new BaseException("core.e1001",String.format("Can not find blob:%s", pid));
		}
		
		String id = ctx.transform($fileId);
		String content = ctx.transform($content);
		
		boolean write = true;
		
		if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(content)){
			if (bm.existFile(id)){
				if (overwrite){
					bm.deleteFile(id);
				}else{
					write = false;
				}
			}
			
			if (write){
				BlobWriter writer = bm.newFile(id);
				try {
					writer.write(content.getBytes(encoding));
				} catch (UnsupportedEncodingException e) {
					throw new BaseException("core.unsupported_encoding",
							String.format("Can not write file %s:%s", id,e.getMessage()));
				}
			}
		}
	}
}