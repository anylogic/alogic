package com.alogic.vfs.xscript;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.fileupload.FileItem;
import com.alogic.blob.BlobManager;
import com.alogic.blob.BlobWriter;
import com.alogic.blob.naming.BlobManagerFactory;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 将上传文件存储到BlobManager
 * @author yyduan
 *
 */
public class UploadToBlob extends AbstractLogiclet{
	protected String pid = "$upload";
	protected String blobId = "default";
	protected String id;
	protected String $fileId = "";
	protected int bufferSize = 10 * 1024;
	
	public UploadToBlob(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		id = PropertiesConstants.getString(p,"id","$" + getXmlTag(),true);
		bufferSize = PropertiesConstants.getInt(p, "bufferSize", bufferSize);
		blobId = PropertiesConstants.getString(p,"blobId",blobId,true);
		$fileId = PropertiesConstants.getRaw(p,"fileId","");
	}

	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		FileItem fileItem = ctx.getObject(pid);
		if (fileItem == null){
			throw new BaseException("core.e1001","It must be in a upload-scan context,check your together script.");
		}
		
		BlobManager bm = BlobManagerFactory.get(blobId);
		if (bm == null){
			throw new BaseException("core.e1001",String.format("Can not find blob manager:%s", blobId));
		}
		
		BlobWriter writer = bm.newFile(PropertiesConstants.transform(ctx, $fileId, ""));
		OutputStream out = null;
		InputStream in = null;
		try {
			in = fileItem.getInputStream();
			out = writer.getOutputStream();
			int size = 0;
			byte[] buffer = new byte[bufferSize];
			while ((size = in.read(buffer)) != -1) {
				out.write(buffer, 0, size);
			}
			out.flush();
			ctx.SetValue(id, "true");
		} catch (Exception ex) {
			ctx.SetValue(id, "false");
			throw new BaseException("core.e1004", ex.getMessage());
		} finally {
			IOTools.close(in);
			writer.finishWrite(out);
		}
	}
}