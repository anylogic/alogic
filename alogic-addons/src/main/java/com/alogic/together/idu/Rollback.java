package com.alogic.together.idu;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;

/**
 * 回滚事务
 * 
 * @author duanyy
 *
 */
public class Rollback extends DBOperation {

	public Rollback(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	protected void onExecute(Connection conn, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		try {
			conn.rollback();
		} catch (SQLException e) {
			logger.error("Failed to rollback transaction",e);
		}
	}

}
