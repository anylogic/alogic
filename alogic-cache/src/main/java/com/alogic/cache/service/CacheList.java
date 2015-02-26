package com.alogic.cache.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.cache.context.CacheSource;
import com.alogic.cache.core.CacheStore;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 列出当前存在的cache列表
 * 
 * @author duanyy
 * @since 1.6.3.3
 * 
 */
public class CacheList extends AbstractServant {

	protected int onXml(Context ctx) throws Exception{
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		CacheSource src = CacheSource.get();
		
		CacheStore[] caches = src.current();
		for (CacheStore cache:caches){
			Element eleCache = doc.createElement("cache");
			cache.report(eleCache);
			root.appendChild(eleCache);
		}
		
		return 0;
	}
	protected int onJson(Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		List<Object> list = new ArrayList<Object>();
		
		CacheSource src = CacheSource.get();
		
		CacheStore[] caches = src.current();
		for (CacheStore cache:caches){
			Map<String,Object> map = new HashMap<String,Object>();
			cache.report(map);
			list.add(map);
		}
		
		msg.getRoot().put("cache", list);
		
		return 0;
	}
	
	@Override
	protected void onDestroy() {
		
	}
	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

}
