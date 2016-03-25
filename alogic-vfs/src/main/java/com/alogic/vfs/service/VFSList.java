package com.alogic.vfs.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.vfs.context.FileSystemSource;
import com.alogic.vfs.core.VirtualFileSystem;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 列出当前活跃的VFS域
 * 
 * @author duanyy
 *
 */
public class VFSList extends AbstractServant {

	@Override
	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		FileSystemSource src = FileSystemSource.get();
		
		Collection<VirtualFileSystem> vfss = src.current();
		for (VirtualFileSystem vfs:vfss){
			Element ele = doc.createElement("vfs");
			vfs.report(ele);
			root.appendChild(ele);
		}
		
		return 0;
	}
	
	@Override
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		List<Object> list = new ArrayList<Object>(); // NOSONAR
		
		FileSystemSource src = FileSystemSource.get();
		
		Collection<VirtualFileSystem> vfss = src.current();
		for (VirtualFileSystem vfs:vfss){
			Map<String,Object> map = new HashMap<String,Object>(); // NOSONAR
			vfs.report(map);
			list.add(map);
		}
		
		msg.getRoot().put("vfs", list);
		
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
