package com.alogic.blob.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.blob.context.BlobManagerSource;
import com.alogic.blob.core.BlobManager;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 查询指定id的BlobManager
 * 
 * @author duanyy
 * @since  1.6.4.4
 * 
 */ 
public class BlobQuery extends AbstractServant {

	protected int onXml(Context ctx) throws Exception{
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		String id = getArgument("id",ctx);
		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		BlobManagerSource src = BlobManagerSource.get();
		BlobManager found = src.get(id);
		if (found == null){
			throw new ServantException("user.data_not_found","Can not find the blob manager :" + id);
		}
		
		Element elem = doc.createElement("blob");
		found.report(elem);
		root.appendChild(elem);

		return 0;
	}
	
	protected int onJson(Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		String id = getArgument("id",ctx);
		
		BlobManagerSource src = BlobManagerSource.get();
		BlobManager found = src.get(id);
		if (found == null){
			throw new ServantException("user.data_not_found","Can not find the blob manager :" + id);
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		found.report(map);
		msg.getRoot().put("blob", map);
		
		return 0;
	}
	
	@Override
	protected void onDestroy() {
		
	}
	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

}