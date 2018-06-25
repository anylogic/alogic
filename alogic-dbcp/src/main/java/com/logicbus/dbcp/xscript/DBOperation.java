package com.logicbus.dbcp.xscript;

import java.sql.Connection;
import java.util.Map;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 数据库操作组件
 * 
 * @since 1.6.10.5
 * 
 * @version 1.6.11.27 [20180417 duanyy] <br>
 * - 增加debug参数 <br>
 */
public abstract class DBOperation extends AbstractLogiclet{
	protected String dbconn = "dbconn";
	protected boolean debug = false;
	public DBOperation(String tag, Logiclet p) {
		super(tag, p);
	}
	
	public void configure(Properties p){
		super.configure(p);
		dbconn = PropertiesConstants.getString(p,"dbconn", dbconn);
		debug = PropertiesConstants.getBoolean(p,"debug", debug,true);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		Connection conn = ctx.getObject(dbconn);
		if (conn == null){
			throw new BaseException("core.e1001","It must be in a db context,check your together script.");
		}
		
		onExecute(conn,root,current,ctx,watcher);
	}

	@SuppressWarnings("unchecked")
	protected void onExecute(Connection conn, XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher){
		if (current instanceof JsonObject){
			onExecute(conn,(Map<String,Object>)root.getContent(),(Map<String,Object>)current.getContent(),ctx,watcher);
		}
	}
	
	protected void onExecute(Connection conn, Map<String,Object> root,Map<String,Object> current, LogicletContext ctx,
			ExecuteWatcher watcher){
		throw new BaseException("core.e1000",
				String.format("Tag %s does not support protocol %s",this.getXmlTag(),root.getClass().getName()));
	}
}