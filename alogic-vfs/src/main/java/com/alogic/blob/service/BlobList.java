package com.alogic.blob.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.blob.BlobManager;
import com.alogic.blob.naming.BlobManagerFactory;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 查询活跃的BlobManager列表
 * 
 * @author duanyy
 *
 * @since 1.6.4.4
 * @version 1.6.4.18 [duanyy 20151218] <br>
 * - 增加自动图标集 <br>
 */
public class BlobList extends AbstractServant {

	@Override
	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		BlobManagerFactory f = BlobManagerFactory.get();
		Collection<BlobManager> current = f.current();
		for (BlobManager instance:current){
			Element elem = doc.createElement("blob");
			instance.report(elem);
			root.appendChild(elem);
		}
		
		return 0;
	}
	@Override
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		List<Object> list = new ArrayList<Object>(); // NOSONAR
		
		BlobManagerFactory f = BlobManagerFactory.get();
		Collection<BlobManager> current = f.current();
		for (BlobManager instance:current){
			Map<String,Object> map = new HashMap<String,Object>();  // NOSONAR
			instance.report(map);
			list.add(map);
		}
		
		msg.getRoot().put("blob", list);
		
		return 0;
	}
	
	@Override
	protected void onDestroy() {
		// Nothing to do
	}
	
	@Override
	protected void onCreate(ServiceDescription sd){
		// Nothing to do
	}

}
