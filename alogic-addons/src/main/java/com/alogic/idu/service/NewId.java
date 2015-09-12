package com.alogic.idu.service;

import java.sql.Connection;

import com.alogic.idu.util.IDUBase;
import com.alogic.sequence.client.SeqTool;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 获取指定域的序列号
 * 
 * @author duanyy
 * @since 1.6.4.6
 */
public class NewId extends IDUBase {

	@Override
	protected void onCreate(ServiceDescription sd, Properties p)
			throws ServantException {
		sequenceId = PropertiesConstants.getString(p, "sequence.id", sequenceId,true);
	}

	@Override
	protected void doIt(Context ctx, JsonMessage msg, Connection conn)
			throws Exception {
		String userId = getArgument("user.id","",ctx);
		if (!checkPrivilege(userId)){
			throw new ServantException("core.unauthorized","无权访问本服务，用户:" + userId);
		}
		
		long newId = SeqTool.nextLong(sequenceId);
		msg.getRoot().put("id",newId);
		bizLog(conn, userId, ctx.getClientIp(), String.valueOf(newId));
	}
	
	protected String sequenceId = "default";
}