package com.alogic.vfs.xscript;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.lang3.StringUtils;
import com.alogic.blob.BlobManager;
import com.alogic.blob.BlobReader;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 装入blob文件内容
 * @author yyduan
 * @since 1.6.11.53 
 */
public class BlobLoad extends AbstractLogiclet{
	protected String pid = "$blob";
	protected String $fileId = "";
	protected String $id = "$blob-load";
	protected String encoding = "utf-8";
	protected int contentLength = 1024;
	protected String dft = "";
	
	public BlobLoad(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		$id = PropertiesConstants.getRaw(p,"id",$id);
		$fileId = PropertiesConstants.getRaw(p, "fileId", $fileId);
		encoding = PropertiesConstants.getString(p,"encoding",encoding,true);
		contentLength = PropertiesConstants.getInt(p,"contentLength",contentLength);
		contentLength = contentLength <= 0? 1024:contentLength;		
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		BlobManager bm = ctx.getObject(pid);
		if (bm == null){
			throw new BaseException("core.e1001",String.format("Can not find blob:%s", pid));
		}
		
		String fileId = ctx.transform($fileId);
		String id = ctx.transform($id);
		
		if (StringUtils.isNotEmpty(fileId)){
			BlobReader reader = bm.getFile(fileId);
			if (reader != null){
				InputStream in = reader.getInputStream(0);
				if (in == null){
					ctx.SetValue(id, ctx.transform(dft));
				}else{
					try {
						byte[] content = new byte[contentLength];
						int realLength = in.read(content);
						ctx.SetValue(id, new String(content,0,realLength,encoding));
					}catch (IOException e){
						throw new BaseException("core.io_exception",
								String.format("Can not read file %s:%s", id,e.getMessage()));
					}finally{
						reader.finishRead(in);
					}
				}				
			}else{
				ctx.SetValue(id, ctx.transform(dft));
			}
		}else{
			ctx.SetValue(id, ctx.transform(dft));
		}
	}
}
