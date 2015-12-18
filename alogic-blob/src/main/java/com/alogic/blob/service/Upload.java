package com.alogic.blob.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.alogic.blob.client.BlobTool;
import com.alogic.blob.core.BlobInfo;
import com.alogic.blob.core.BlobManager;
import com.alogic.blob.core.BlobWriter;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.MultiPartForm;
import com.logicbus.backend.message.MultiPartForm.FileItemHandler;
import com.logicbus.models.servant.ServiceDescription;

/**
 * Blob文件上传
 * @author duanyy
 * @version 1.6.4.18 [duanyy 20151218] <br>
 * - 增加自动图标集 <br>
 */
public class Upload extends Servant implements FileItemHandler{
	protected byte [] buffer = null;
	protected String domain = "default";
	protected BlobManager blobManager = null;
	
	@Override
	public void create(ServiceDescription sd){
		super.create(sd);
		Properties p = sd.getProperties();
		
		int bufferSize = PropertiesConstants.getInt(p, "bufferSize", 10240,true);
		
		buffer = new byte [bufferSize];
		
		domain = PropertiesConstants.getString(p,"blob.domain",domain);
		
		blobManager = BlobTool.getBlobManager(domain);
		if (blobManager == null){
			throw new ServantException("core.blob_not_found","Can not find a blob manager named: " + domain);
		}
	}
	
	@Override
	public int actionProcess(Context ctx) throws Exception{
		MultiPartForm msg = (MultiPartForm) ctx.asMessage(MultiPartForm.class);
		
		msg.handle(this);
		
		return 0;
	}
	@Override
	public void handle(FileItem item, Map<String, Object> result) {
		BlobWriter writer = blobManager.newFile(item.getContentType());
		OutputStream out = writer.getOutputStream();
		InputStream in = null;
		try {
			in = item.getInputStream();
			
			try {
		        int size=0;  
		        
		        while((size=in.read(buffer))!=-1)  
		        {  
		        	out.write(buffer, 0, size);
		        }  
			}finally{
				IOTools.close(in,out);
			}
			
			BlobInfo info = writer.getBlobInfo();
			result.put("fileId", info.id());
			result.put("contentType", info.contentType());
			result.put("md5", info.md5());
			result.put("sucessful", "true");
			
			blobManager.commit(writer);
		}catch (Exception ex){
			logger.error("Error when handle file:" + item.getName(), ex);
			result.put("sucessful", "false");		
			blobManager.cancel(writer);
		}
	}
}
