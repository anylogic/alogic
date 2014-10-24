package com.logicbus.together.logiclet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.Message;
import com.logicbus.dbcp.sql.SQLTools;
import com.logicbus.together.AbstractLogiclet;
import com.logicbus.together.ExecuteWatcher;
import com.logicbus.together.LogicletFactory;


/**
 * 通用SQL查询
 * 
 * @author duanyy
 * @since 1.1.0
 * 
 * @version 1.2.0 增加对JSON支持
 */
public class SQLQuery extends AbstractLogiclet {

	
	protected void onCompile(Element config, Properties myProps,LogicletFactory factory)
			throws ServantException {
		sql = config.getAttribute("sql");
	}

	
	protected void onExecute(Element target, Message msg, Context ctx,ExecuteWatcher watcher)
			throws ServantException {
		if (sql == null || sql.length() <= 0){
			return ;
		}
		
		String _sql = ctx.transform(sql);
		
		logger.info("Execute SQL:" + _sql);
		
		Connection conn = ctx.getConnection();
		Statement stmt = null;
		try {
			stmt = SQLTools.createStatement(conn);
			ResultSet rs = SQLTools.executeQuery(stmt, _sql);
			Document doc = target.getOwnerDocument();
			while (rs.next()){
				Element row = doc.createElement("row");
				ResultSetMetaData metadata = rs.getMetaData();
				int column_count = metadata.getColumnCount();
				for (int i = 1 ; i < column_count + 1; i ++)
				{
					Object obj = rs.getObject(i);
					if (obj != null)
					{
						row.setAttribute(metadata.getColumnName(i).toLowerCase(), obj.toString());
					}
				}				
				target.appendChild(row);
			}
			SQLTools.close(rs);			
		}catch (Exception ex){
			throw new ServantException("core.sqlerror","Error occurs when querying : " + _sql);
		}finally{
			SQLTools.close(stmt);
		}
	}
	

	protected String sql;


	@SuppressWarnings({ "rawtypes", "unchecked" })
	
	protected void onExecute(Map target, Message msg, Context ctx,
			ExecuteWatcher watcher) throws ServantException {
		if (sql == null || sql.length() <= 0){
			return ;
		}
		
		String _sql = ctx.transform(sql);
		
		logger.info("Execute SQL:" + _sql);
		
		Connection conn = ctx.getConnection();
		Statement stmt = null;
		try {
			stmt = SQLTools.createStatement(conn);
			ResultSet rs = SQLTools.executeQuery(stmt, _sql);
			List list = new ArrayList();
			while (rs.next()){
				Map row = new HashMap();
				ResultSetMetaData metadata = rs.getMetaData();
				int column_count = metadata.getColumnCount();
				for (int i = 1 ; i < column_count + 1; i ++)
				{
					Object obj = rs.getObject(i);
					if (obj != null)
					{
						row.put(metadata.getColumnName(i).toLowerCase(), obj.toString());
					}
				}				
				list.add(row);	
			}
			
			target.put("result", list);
			
			SQLTools.close(rs);			
		}catch (Exception ex){
			throw new ServantException("core.sqlerror","Error occurs when querying : " + _sql);
		}finally{
			SQLTools.close(stmt);
		}
	}
}
