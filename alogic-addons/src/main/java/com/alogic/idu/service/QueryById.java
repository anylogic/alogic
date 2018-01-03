package com.alogic.idu.service;

import java.util.HashMap;
import java.util.Map;

import com.alogic.cache.core.CacheStore;
import com.alogic.cache.core.MultiFieldObject;
import com.alogic.idu.util.Base;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 通过ID查询指定的记录
 * 
 * @author duanyy
 * @since 1.6.4.6
 * 
 * @deprecated
 */
public class QueryById extends Base {
	protected String rootName = "data";
	@Override
	protected void onCreate(ServiceDescription sd, Properties p) {
		rootName = PropertiesConstants.getString(p, "data.root", rootName);
	}

	@Override
	protected int onJson(Context ctx, JsonMessage msg)  {
		String id = getArgument("id",ctx);
		
		CacheStore cache = getCacheStore();
		
		MultiFieldObject found = cache.get(id, true);
		if (found == null){
			throw new ServantException("clnt.e2007","Can not find object,id=" + id);
		}
	
		Map<String,Object> data = new HashMap<String,Object>();
		
		found.toJson(data);
		
		msg.getRoot().put(rootName, data);
		return 0;
	}
}
