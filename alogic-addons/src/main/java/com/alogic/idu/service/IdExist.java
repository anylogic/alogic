package com.alogic.idu.service;

import java.sql.Connection;

import com.alogic.idu.util.IDUBase;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.dbcp.sql.DBTools;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 检查指定ID能否在某个数据表中使用
 * 
 * @author duanyy
 * @since 1.6.4.6
 */
public class IdExist extends IDUBase {

	@Override
	protected void onCreate(ServiceDescription sd, Properties p)
			throws ServantException {
		sqlIdExist = PropertiesConstants.getString(p,"sql.IdExist",sqlIdExist);
	}

	@Override
	protected void doIt(Context ctx, JsonMessage msg, Connection conn)
			throws Exception {
		String id = getArgument("id",ctx);
		
		int exist = DBTools.selectAsInt(conn, sqlIdExist, 1, id);
		if (exist > 0){
			throw new ServantException("core.id_used","The given id has been used.");
		}
	}

	/**
	 * 检查ID是否存在的SQL语句
	 */
	protected String sqlIdExist = "select 1 from dual";
}
