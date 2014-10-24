package com.logicbus.together.logiclet;

import java.sql.Connection;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Properties;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.Message;
import com.logicbus.dbcp.sql.SQLTools;
import com.logicbus.together.AbstractLogiclet;
import com.logicbus.together.ExecuteWatcher;
import com.logicbus.together.LogicletFactory;


/**
 * 执行SQL语句
 * 
 * @author duanyy
 * @since 1.1.0
 * @version 1.2.0 增加对JSON支持
 */
public class SQLUpdate extends AbstractLogiclet {

	
	protected void onCompile(Element config, Properties myProps,LogicletFactory factory)
			throws ServantException {
		NodeList children = XmlTools.getNodeListByPath(config, "sql");
		
		for (int i = 0 , length = children.getLength() ; i < length ; i ++){
			Node n = children.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element e = (Element)n;
			
			String sql = e.getAttribute("sql");
			
			if (sql != null && sql.length() > 0){
				sqls.add(sql);
			}
		}
	}

	
	protected void onExecute(Element target, Message msg, Context ctx,ExecuteWatcher watcher)
			throws ServantException {
		int count = sqls.size(); 
		if (count > 0){
			Connection conn = ctx.getConnection();
			String [] _sqls = new String[sqls.size()];
			
			for (int i = 0 ; i < count ; i ++){
				_sqls[i] = ctx.transform(sqls.get(i));
				
				logger.info("Execute SQL : " + _sqls[i]);
			}
			
			SQLTools.executeBatch(conn, _sqls);
		}

	}

	protected Vector<String> sqls = new Vector<String>();

	@SuppressWarnings("rawtypes")
	
	protected void onExecute(Map target, Message msg, Context ctx,
			ExecuteWatcher watcher) throws ServantException {
		int count = sqls.size(); 
		if (count > 0){
			Connection conn = ctx.getConnection();
			String [] _sqls = new String[sqls.size()];
			
			for (int i = 0 ; i < count ; i ++){
				_sqls[i] = ctx.transform(sqls.get(i));
				
				logger.info("Execute SQL : " + _sqls[i]);
			}
			
			SQLTools.executeBatch(conn, _sqls);
		}
	}
}
