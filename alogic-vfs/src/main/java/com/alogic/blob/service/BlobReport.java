package com.alogic.blob.service;

import java.util.HashMap;
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
 * BlobReport，生成Blob使用情况报告
 * 
 * @author duanyy
 * @since 1.6.3.28
 * 
 * @version 1.6.4.18 [duanyy 20151218] <br>
 * - 增加自动图标集 <br>
 */
public class BlobReport extends AbstractServant {

	@Override
	protected void onDestroy() {
		// Nothing to do
	}

	@Override
	protected void onCreate(ServiceDescription sd) {
		// Nothing to do
	}
	
	@Override
	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);		
		
		Element root = msg.getRoot();		
		Document doc = msg.getDocument();	
		
		String id = getArgument("id","all", ctx);	
		if (id == null || id.length() <= 0 || "all".equals(id)){
			Element source = doc.createElement("source");
			
			BlobManagerFactory f = BlobManagerFactory.get();			
			f.report(source);
			
			root.appendChild(source);
		}else{			
			Element blob = doc.createElement("blob");

			BlobManager manager = BlobManagerFactory.get(id);
			if (manager != null){
				manager.report(blob);
			}
			root.appendChild(blob);
		}
		return 0;
	}

	@Override
	protected int onJson(Context ctx) {
		JsonMessage msg = (JsonMessage) ctx.asMessage(JsonMessage.class);
		Map<String,Object> root = msg.getRoot();

		String id = getArgument("id","all", ctx);
		
		if (id == null || id.length() <= 0 || "all".equals(id)){
			Map<String,Object> source = new HashMap<String,Object>();  // NOSONAR
			
			BlobManagerFactory f = BlobManagerFactory.get();			
			f.report(source);
			
			root.put("source", source);
		}else{
			Map<String,Object> blob = new HashMap<String,Object>(); // NOSONAR
			
			BlobManager manager = BlobManagerFactory.get(id);
			if (manager != null){
				manager.report(blob);
			}
			root.put("blob", blob);
		}
		return 0;
	}
}
