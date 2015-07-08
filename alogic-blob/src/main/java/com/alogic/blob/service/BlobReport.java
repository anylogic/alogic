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
 * BlobReport，生成Blob使用情况报告
 * 
 * @author duanyy
 * @since 1.6.3.28
 */
public class BlobReport extends AbstractServant {

	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

	protected int onXml(Context ctx) throws Exception {
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);		
		
		Element root = msg.getRoot();		
		Document doc = msg.getDocument();	
		
		String id = getArgument("id","all", ctx);	
		if (id == null || id.length() <= 0 || id.equals("all")){
			Element source = doc.createElement("source");
			
			BlobManagerSource managerSource = BlobManagerSource.get();			
			managerSource.report(source);
			
			root.appendChild(source);
		}else{			
			Element blob = doc.createElement("blob");
			
			BlobManagerSource managerSource = BlobManagerSource.get();		
			BlobManager manager = managerSource.get(id);
			if (manager != null){
				manager.report(blob);
			}
			root.appendChild(blob);
		}
		return 0;
	}

	
	protected int onJson(Context ctx) throws Exception {
		JsonMessage msg = (JsonMessage) ctx.asMessage(JsonMessage.class);
		Map<String,Object> root = msg.getRoot();

		String id = getArgument("id","all", ctx);
		
		if (id == null || id.length() <= 0 || id.equals("all")){
			Map<String,Object> source = new HashMap<String,Object>();
			
			BlobManagerSource managerSource = BlobManagerSource.get();			
			managerSource.report(source);
			
			root.put("source", source);
		}else{
			Map<String,Object> blob = new HashMap<String,Object>();
			
			BlobManagerSource managerSource = BlobManagerSource.get();		
			BlobManager manager = managerSource.get(id);
			if (manager != null){
				manager.report(blob);
			}
			root.put("blob", blob);
		}
		return 0;
	}
}
