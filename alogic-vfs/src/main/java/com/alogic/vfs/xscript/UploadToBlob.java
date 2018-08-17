package com.alogic.vfs.xscript;

import java.io.InputStream;

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
 * @version 1.6.11.53 [20180817 duanyy] <br>
 * - BlobManager模型变更 <br>
 */
public class UploadToBlob extends AbstractLogiclet{
	protected String pid = "$upload";
	protected String pBlobId = "$blob";
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
		
		pBlobId = PropertiesConstants.getString(p,"pBlobId",pBlobId,true);
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
		
		BlobManager bm = ctx.getObject(pBlobId);
		if (bm == null){
			bm = BlobManagerFactory.get(blobId);
			if (bm == null){
				throw new BaseException("core.e1001",String.format("Can not find blob manager:%s", blobId));
			}
		}
		
		BlobWriter writer = bm.newFile(PropertiesConstants.transform(ctx, $fileId, ""));
		InputStream in = null;
		try {
			in = fileItem.getInputStream();
			writer.write(in, fileItem.getSize(), false);
			ctx.SetValue(id, "true");
		} catch (Exception ex) {
			ctx.SetValue(id, "false");
			throw new BaseException("core.e1004", ex.getMessage());
		} finally {
			IOTools.close(in);
		}
	}
}