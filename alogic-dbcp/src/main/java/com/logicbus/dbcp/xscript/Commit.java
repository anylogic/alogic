package com.logicbus.dbcp.xscript;

import java.sql.Connection;
import java.sql.SQLException;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;

/**
 * 提交事务
 * 
 * @since 1.6.10.5
 */
public class Commit extends DBOperation {

	public Commit(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	protected void onExecute(Connection conn, XsObject root,XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		try {
			conn.commit();
		} catch (SQLException e) {
			logger.error("Failed to commit transaction",e);
		}
	}

}
