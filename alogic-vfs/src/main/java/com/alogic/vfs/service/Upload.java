package com.alogic.vfs.service;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.alogic.vfs.context.FileSystemSource;
import com.alogic.vfs.core.VirtualFileSystem;
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
 * 向vfs上传文件
 * 
 * @author duanyy
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class Upload extends Servant implements FileItemHandler{
	protected byte [] buffer = null;
	
	@Override
	public void create(ServiceDescription sd){
		super.create(sd);
		Properties p = sd.getProperties();
		
		int bufferSize = PropertiesConstants.getInt(p, "bufferSize", 10240,true);
		
		buffer = new byte [bufferSize];
	}
	
	@Override
	public int actionProcess(Context ctx) throws Exception{
		MultiPartForm msg = (MultiPartForm) ctx.asMessage(MultiPartForm.class);
	
		String fsId = getArgument("domain","default",ctx);
		
		getArgument("path",ctx);
		
		VirtualFileSystem fs = FileSystemSource.get().get(fsId);
		if (fs == null){
			throw new ServantException("core.data_not_found","Can not find a vfs named " +  fsId);
		}		
		
		msg.handle(ctx,fs,this);
		
		return 0;
	}
	
	@Override
	public void handle(Context ctx,Object cookies,FileItem item, Map<String, Object> result) {
		VirtualFileSystem fs = (VirtualFileSystem)cookies;
		String rootPath = getArgument("path","/",ctx);
		
		String newFile = rootPath + File.separatorChar + item.getName();
		
		if (fs.exist(newFile)){
			result.put("sucessful", false);
			result.put("reason", "file exist");
		}else{
			OutputStream out = fs.writeFile(newFile);
			InputStream in = null;
			try {
				if (out == null) {
					result.put("sucessful", false);
					result.put("reason", "file exist or no permission");
				} else {
					in = item.getInputStream();
					int size = 0;
					while ((size = in.read(buffer)) != -1) {
						out.write(buffer, 0, size);
					}
					result.put("sucessful", true);
				}
			}catch (Exception ex){
				result.put("sucessful", false);
				result.put("reason", ex.getMessage());				
				logger.error(ex.getMessage());
			}finally{
				IOTools.close(in);
				fs.finishWrite(newFile, out);
			}
		}
	}
}