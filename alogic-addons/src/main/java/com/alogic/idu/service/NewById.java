package com.alogic.idu.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alogic.cache.core.CacheStore;
import com.alogic.cache.core.MultiFieldObject;
import com.alogic.idu.util.IDUBase;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.dbcp.processor.Preprocessor;
import com.logicbus.dbcp.sql.DBTools;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 在指定表新增记录
 * 
 * @author duanyy
 * @since 1.6.4.6
 */
public class NewById extends IDUBase {

	@Override
	protected void onCreate(ServiceDescription sd, Properties p) {
		sqlInsert = PropertiesConstants.getString(p, "sql.Insert", sqlInsert);
		rootName = PropertiesConstants.getString(p, "data.root", rootName);
		dataId = PropertiesConstants.getString(p,"dataGuard.id", dataId);
		processor = new Preprocessor(sqlInsert);
	}

	@Override
	protected void doIt(Context ctx, JsonMessage msg, Connection conn)
			 {
		String userId = getArgument("user.id","",ctx);
		String id = getArgument("id",ctx);
		String dataGuardObject = getArgument(dataId,id,ctx);
		if (!checkPrivilege(userId,dataGuardObject)){
			throw new ServantException("core.e1010","无权访问本服务，用户:" + userId);
		}
		
		CacheStore cache = getCacheStore();
		
		MultiFieldObject found = cache.get(id, true);
		if (found != null){
			throw new ServantException("clnt.e2008","The object exists,id=" + id);
		}
		
		List<Object> data = new ArrayList<Object>();
		String sql = processor.process(ctx, data);

		if (data.size() > 0){
			DBTools.insert(conn, sql, data.toArray());
		}else{
			DBTools.insert(conn, sql);
		}
		
		found = cache.get(id, true);
		if (found != null){
			Map<String,Object> output = new HashMap<String,Object>();
			found.toJson(output);
			msg.getRoot().put(rootName, output);		
		}
		
		bizLog(conn, userId, ctx.getClientIp(), id,ctx);
	}

	protected String sqlInsert = "";
	
	protected Preprocessor processor = null;
	
	protected String rootName = "data";
	
	protected String dataId = "id";
}