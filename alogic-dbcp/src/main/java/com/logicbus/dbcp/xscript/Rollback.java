package com.logicbus.dbcp.xscript;

import java.sql.Connection;
import java.sql.SQLException;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;

/**
 * 回滚事务
 * 
 * @author duanyy
 * 
 * @since 1.6.10.5
 * 
 */
public class Rollback extends DBOperation {

	public Rollback(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	protected void onExecute(Connection conn, XsObject root,XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		try {
			conn.rollback();
		} catch (SQLException e) {
			logger.error("Failed to rollback transaction",e);
		}
	}

}
