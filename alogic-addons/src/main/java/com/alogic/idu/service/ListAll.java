package com.alogic.idu.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alogic.idu.util.IDUBase;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.dbcp.processor.Preprocessor;
import com.logicbus.dbcp.sql.DBTools;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 查询多条记录
 * 
 * @author duanyy
 * @since 1.6.4.6
 * @deprecated
 */
public class ListAll extends IDUBase {

	@Override
	protected void onCreate(ServiceDescription sd, Properties p){
		rootName = PropertiesConstants.getString(p, "data.root", rootName);
		sqlQuery = PropertiesConstants.getString(p, "sql.Query", rootName);
		
		processor = new Preprocessor(sqlQuery);
	}

	@Override
	protected void doIt(Context ctx, JsonMessage msg, Connection conn)
			 {
		List<Object> data = new ArrayList<Object>();
		
		String sql = processor.process(ctx, data);
		List<Map<String,Object>> result = null;
		
		if (data.size() <= 0){
			result = DBTools.listAsObject(conn, sql);
		}else{
			result = DBTools.listAsObject(conn, sql,data.toArray());
		}
		
		msg.getRoot().put(rootName, result);
	}
	
	protected String rootName = "data";
	
	protected String sqlQuery = "";
	
	protected Preprocessor processor = null;
}
