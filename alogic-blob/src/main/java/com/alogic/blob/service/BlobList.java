package com.alogic.blob.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
 * 查询活跃的BlobManager列表
 * 
 * @author duanyy
 *
 * @since 1.6.4.4
 * 
 */
public class BlobList extends AbstractServant {

	protected int onXml(Context ctx) throws Exception{
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		BlobManagerSource src = BlobManagerSource.get();
		
		Collection<BlobManager> current = src.current();
		for (BlobManager instance:current){
			Element elem = doc.createElement("blob");
			instance.report(elem);
			root.appendChild(elem);
		}
		
		return 0;
	}
	
	protected int onJson(Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		List<Object> list = new ArrayList<Object>();
		
		BlobManagerSource src = BlobManagerSource.get();
		
		Collection<BlobManager> current = src.current();
		for (BlobManager instance:current){
			Map<String,Object> map = new HashMap<String,Object>();
			instance.report(map);
			list.add(map);
		}
		
		msg.getRoot().put("blob", list);
		
		return 0;
	}
	
	@Override
	protected void onDestroy() {
		
	}
	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

}
