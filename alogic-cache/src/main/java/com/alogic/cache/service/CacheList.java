package com.alogic.cache.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.cache.context.CacheSource;
import com.alogic.cache.core.CacheStore;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 列出当前存在的cache列表
 * 
 * @author duanyy
 * @since 1.6.3.3
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 */
public class CacheList extends AbstractServant {

	@Override
	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		CacheSource src = CacheSource.get();
		
		Collection<CacheStore> caches = src.current();
		for (CacheStore cache:caches){
			Element eleCache = doc.createElement("cache");
			cache.report(eleCache);
			root.appendChild(eleCache);
		}
		
		return 0;
	}
	
	@Override
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		List<Object> list = new ArrayList<Object>(); // NOSONAR
		
		CacheSource src = CacheSource.get();
		
		Collection<CacheStore> caches = src.current();
		for (CacheStore cache:caches){
			Map<String,Object> map = new HashMap<String,Object>(); // NOSONAR
			cache.report(map);
			list.add(map);
		}
		
		msg.getRoot().put("cache", list);
		
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
