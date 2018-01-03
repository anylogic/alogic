package com.alogic.together.idu;

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
import com.logicbus.backend.ServantException;

/**
 * 数据库操作组件
 * 
 * @author duanyy
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * @deprecated
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
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		Connection conn = ctx.getObject(dbconn);
		if (conn == null){
			throw new ServantException("core.e1001","It must be in a db context,check your together script.");
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