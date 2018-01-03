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
 * 更新指定ID的数据表
 * 
 * @author duanyy
 * @since 1.6.4.6
 * 
 * @deprecated
 */
public class UpdateById extends IDUBase {

	@Override
	protected void onCreate(ServiceDescription sd, Properties p) {
		sqlUpdate = PropertiesConstants.getString(p, "sql.Update", sqlUpdate);
		rootName = PropertiesConstants.getString(p, "data.root", rootName);
		dataId = PropertiesConstants.getString(p,"dataGuard.id", dataId);
		processor = new Preprocessor(sqlUpdate);
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
		
		List<Object> data = new ArrayList<Object>();
		String sql = processor.process(ctx, data);

		if (data.size() > 0){
			DBTools.update(conn, sql, data.toArray());
		}else{
			DBTools.update(conn, sql);
		}
		
		clearCache(id);
		
		CacheStore cache = getCacheStore();
		
		MultiFieldObject found = cache.get(id, true);
		if (found != null){
			Map<String,Object> output = new HashMap<String,Object>();	
			found.toJson(output);
			msg.getRoot().put(rootName, output);
		}
		
		bizLog(conn, userId, ctx.getClientIp(), id,ctx);
	}

	protected String sqlUpdate = "";
	
	protected Preprocessor processor = null;
	
	protected String rootName = "data";
	
	protected String dataId = "id";
}
