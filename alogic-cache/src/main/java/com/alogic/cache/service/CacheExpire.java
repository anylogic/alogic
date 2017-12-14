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
 * 过期指定的cache或者cache中指定的数据
 * 
 * @author duanyy
 * @since 1.6.3.3
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 */
public class CacheExpire  extends AbstractServant {

	@Override
	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		String id = getArgument("cacheId",ctx);
		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		CacheSource src = CacheSource.get();
		
		CacheStore found = src.get(id);
		if (found == null){
			throw new ServantException("clnt.e2007","Can not find a cache :" + id);
		}
		
		String objectId = getArgument("objectId","",ctx);
		if (objectId == null || objectId.length() <= 0){
			//过期所有的数据
			found.expireAll();
		}else{
			found.expire(objectId);
		}
		
		Element eleCache = doc.createElement("cache");
		found.report(eleCache);
		root.appendChild(eleCache);

		return 0;
	}
	
	@Override
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		String id = getArgument("cacheId",ctx);
		
		CacheSource src = CacheSource.get();
		
		CacheStore found = src.get(id);
		if (found == null){
			throw new ServantException("clnt.e2007","Can not find a cache :" + id);
		}
		
		String objectId = getArgument("objectId","",ctx);
		if (objectId == null || objectId.length() <= 0){
			//过期所有的数据
			found.expireAll();
		}else{
			found.expire(objectId);
		}
		
		Map<String,Object> map = new HashMap<String,Object>(); // NOSONAR
		found.report(map);
		msg.getRoot().put("cache", map);
		
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