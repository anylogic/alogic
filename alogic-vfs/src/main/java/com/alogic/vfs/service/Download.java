package com.alogic.vfs.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.alogic.vfs.context.FileSystemSource;
import com.alogic.vfs.core.VirtualFileSystem;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.Message;
import com.logicbus.models.servant.ServiceDescription;

/**
 * vfs的文件下载
 * @author duanyy
 *
 */
public class Download extends Servant {
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
		ctx.asMessage(FileMessage.class);
		
		String path = getArgument("path","/",ctx);
		String fsId = getArgument("domain","default",ctx);
		
		VirtualFileSystem fs = FileSystemSource.get().get(fsId);
		
		if (fs == null){
			throw new ServantException("core.data_not_found","Can not find a vfs named " +  fsId);
		}

		InputStream in = null;
		OutputStream out = ctx.getOutputStream();
		
		try {
			in = fs.readFile(path);
			if (in == null){
				throw new ServantException("core.data_not_found","Can not find the file: " +  path);
			}
	        int size=0;  
	        
	        while((size=in.read(buffer))!=-1)  
	        {  
	        	out.write(buffer, 0, size);
	        }  
		}finally{
			fs.finishRead(path, in);
		}
		
		return 0;
	}
	
	/**
	 * blob文件下载定制Message,不做啥事
	 * 
	 * @author duanyy
	 *
	 */
	public static class FileMessage implements Message {
		@Override
		public void finish(Context ctx, boolean closeStream) {
			if (!"core.ok".equals(ctx.getReturnCode())){
				throw new ServantException(ctx.getReturnCode(),ctx.getReason());
			}
			OutputStream out = null;
			try {
				out = ctx.getOutputStream();
			} catch (IOException e) {
				logger.error("IO Exception",e);
			}finally{
				if (closeStream){
					IOTools.close(out);
				}
			}
		}
		@Override
		public void init(Context ctx) {
			// Nothing to do
		}
		@Override
		public String getContentType() {
			return "unknown";
		}
		@Override
		public long getContentLength() {
			return 0;
		}
	}
}