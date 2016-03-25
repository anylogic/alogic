package com.alogic.vfs.service;

import java.util.HashMap;
import java.util.Map;

import com.alogic.vfs.context.FileSystemSource;
import com.alogic.vfs.core.VirtualFileSystem;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.models.servant.ServiceDescription;


/**
 * 列出指定目录的文件列表
 * 
 * @author duanyy
 *
 */
public class FileList extends AbstractServant{

	@Override
	protected void onDestroy() {
		// nothing to do
	}

	@Override
	protected void onCreate(ServiceDescription sd) {
		// nothing to do
	}
	
	protected int onXml(Context ctx){
		throw new ServantException("core.not_supported",
				"Protocol XML is not suppurted.");		
	}

	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage) ctx.asMessage(JsonMessage.class);
		
		String path = getArgument("path","/",ctx);
		String fsId = getArgument("domain","default",ctx);
		String pattern = getArgument("pattern","*",ctx);
		int offset = getArgument("offset",0,ctx);
		int limit = getArgument("limit",30,ctx);		
		
		VirtualFileSystem fs = FileSystemSource.get().get(fsId);
		
		if (fs == null){
			throw new ServantException("core.data_not_found","Can not find a vfs named " +  fsId);
		}
		
		Map<String,Object> vfs = new HashMap<String,Object>();
		
		fs.listFiles(path, pattern, vfs, offset, limit);
		
		msg.getRoot().put("vfs", vfs);
		return 0;
	}	

}
