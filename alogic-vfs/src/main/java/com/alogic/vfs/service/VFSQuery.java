package com.alogic.vfs.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.vfs.context.FileSystemSource;
import com.alogic.vfs.core.VirtualFileSystem;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 查询指定的VFS
 * 
 * @author duanyy
 *
 */
public class VFSQuery extends AbstractServant {

	@Override
	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		String id = getArgument("domain",ctx);
		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		FileSystemSource src = FileSystemSource.get();
		
		VirtualFileSystem found = src.get(id);
		if (found == null){
			throw new ServantException("user.data_not_found","Can not find a vfs :" + id);
		}
		
		Element ele = doc.createElement("vfs");
		found.report(ele);
		root.appendChild(ele);

		return 0;
	}
	
	@Override
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		String id = getArgument("domain",ctx);
		
		FileSystemSource src = FileSystemSource.get();
		
		VirtualFileSystem found = src.get(id);
		if (found == null){
			throw new ServantException("user.data_not_found","Can not find a vfs :" + id);
		}
		
		Map<String,Object> map = new HashMap<String,Object>(); // NOSONAR
		found.report(map);
		msg.getRoot().put("vfs", map);
		
		return 0;
	}
	
	@Override
	protected void onDestroy() {
		// nothing to do
	}
	
	@Override
	protected void onCreate(ServiceDescription sd) {
		// nothing to do
	}

}
