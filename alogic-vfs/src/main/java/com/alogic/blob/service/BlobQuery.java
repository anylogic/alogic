package com.alogic.blob.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.blob.BlobManager;
import com.alogic.blob.naming.BlobManagerFactory;
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
 * @version 1.6.4.18 [duanyy 20151218] <br>
 * - 增加自动图标集 <br>
 */ 
public class BlobQuery extends AbstractServant {

	@Override
	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		String id = getArgument("id",ctx);
		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		BlobManager found = BlobManagerFactory.get(id);
		if (found == null){
			throw new ServantException("clnt.e2007","Can not find the blob manager :" + id);
		}
		
		Element elem = doc.createElement("blob");
		found.report(elem);
		root.appendChild(elem);

		return 0;
	}
	@Override
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		String id = getArgument("id",ctx);
		
		BlobManager found = BlobManagerFactory.get(id);
		if (found == null){
			throw new ServantException("clnt.e2007","Can not find the blob manager :" + id);
		}
		
		Map<String,Object> map = new HashMap<String,Object>(); // NOSONAR
		found.report(map);
		msg.getRoot().put("blob", map);
		
		return 0;
	}
	
	@Override
	protected void onDestroy() {
		// nothing to do
	}
	@Override
	protected void onCreate(ServiceDescription sd)  {
		// nothing to do
	}

}