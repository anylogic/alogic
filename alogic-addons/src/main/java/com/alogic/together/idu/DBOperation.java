package com.alogic.together.idu;

import java.sql.Connection;
import java.util.Map;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.ServantException;

/**
 * 数据库操作组件
 * 
 * @author duanyy
 *
 */
public abstract class DBOperation extends AbstractLogiclet{
	protected String dbconn = "dbconn";
	public DBOperation(String tag, Logiclet p) {
		super(tag, p);
	}
	
	public void configure(Properties p){
		super.configure(p);
		dbconn = PropertiesConstants.getString(p,"dbconn", dbconn);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		Connection conn = ctx.getObject(dbconn);
		if (conn == null){
			throw new ServantException("core.no_db_connection","It must be in a db context,check your together script.");
		}
		
		onExecute(conn,root,current,ctx,watcher);
	}

	protected abstract void onExecute(Connection conn, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher);
}