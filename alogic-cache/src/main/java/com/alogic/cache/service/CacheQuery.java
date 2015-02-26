package com.alogic.cache.service;

import java.util.HashMap;
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
 * 查询指定cache信息
 * 
 * @author duanyy
 * @since 1.6.3.3
 */
public class CacheQuery extends AbstractServant {

	protected int onXml(Context ctx) throws Exception{
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		String id = getArgument("cacheId",ctx);
		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		CacheSource src = CacheSource.get();
		
		CacheStore found = src.get(id);
		if (found == null){
			throw new ServantException("user.data_not_found","Can not find a cache :" + id);
		}
		
		Element eleCache = doc.createElement("cache");
		found.report(eleCache);
		root.appendChild(eleCache);

		return 0;
	}
	
	protected int onJson(Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		String id = getArgument("cacheId",ctx);
		
		CacheSource src = CacheSource.get();
		
		CacheStore found = src.get(id);
		if (found == null){
			throw new ServantException("user.data_not_found","Can not find a cache :" + id);
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		found.report(map);
		msg.getRoot().put("cache", map);
		
		return 0;
	}
	
	@Override
	protected void onDestroy() {
		
	}
	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

}
