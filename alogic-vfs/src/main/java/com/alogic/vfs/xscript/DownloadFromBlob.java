package com.alogic.vfs.xscript;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.blob.BlobManager;
import com.alogic.blob.BlobReader;
import com.alogic.blob.naming.BlobManagerFactory;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;

/**
 * 从blob中下载
 * @author yyduan
 * @since 1.6.11.12
 * 
 * @version 1.6.11.29 [20180510 duanyy] <br>
 * - 优化错误处理 <br>
 */
public class DownloadFromBlob extends AbstractLogiclet{
	protected String pid = "$context";
	protected String blobId = "default";
	protected String id;
	protected int bufferSize = 10 * 1024;
	protected String $fileId = "";
	protected String $contentType = "text/plain";
	
	public DownloadFromBlob(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		id = PropertiesConstants.getString(p,"id","$" + getXmlTag(),true);
		bufferSize = PropertiesConstants.getInt(p, "bufferSize", bufferSize);
		blobId = PropertiesConstants.getString(p,"blobId",blobId,true);
		$fileId = PropertiesConstants.getRaw(p, "fileId", $fileId);
		$contentType = PropertiesConstants.getRaw(p, "contentType", $contentType);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		Context serviceContext = ctx.getObject(pid);
		if (serviceContext == null){
			throw new BaseException("core.e1001","It must be in a DownloadTogetherServant servant,check your together script.");
		}
		
		BlobManager bm = BlobManagerFactory.get(blobId);
		if (bm == null){
			throw new BaseException("core.e1001",String.format("Can not find blob manager:%s", blobId));
		}
		
		String fileId = PropertiesConstants.transform(ctx, $fileId, "");
		BlobReader reader = bm.getFile(fileId);
		if (reader == null){
			throw new BaseException("core.e1001",String.format("Can not find file:%s", fileId));
		}
		InputStream in = null;
		try {
			String contentType = PropertiesConstants.transform(ctx, $contentType, "text/plain");
			if (StringUtils.isEmpty(contentType)){
				contentType = reader.getBlobInfo().getContentType();
			}
			serviceContext.setResponseContentType(contentType);
			OutputStream out = serviceContext.getOutputStream();
			in = reader.getInputStream(0);
			int size = 0;
			byte[] buffer = new byte[bufferSize];
			while ((size = in.read(buffer)) != -1) {
				out.write(buffer, 0, size);
			}
			out.flush();
			ctx.SetValue(id, "true");
		} catch (Exception ex) {
			logger.error(ExceptionUtils.getStackTrace(ex));
			ctx.SetValue(id, "false");
		} finally {
			reader.finishRead(in);
		}
	}
}