package com.alogic.together.idu;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;

/**
 * DBConnection
 * 
 * @author duanyy
 *
 * @version 1.6.5.30 [duanyy 20160720] <br>
 * - 将事务操作交给事务语句去做 <br>
 * 
 */
public class DBConnection extends Segment {
	
	protected String dbcpId;
	protected String dbconn = "dbconn";
	protected boolean autoCommit = true;
	
	public DBConnection(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		dbcpId = PropertiesConstants.getString(p,"dbcpId","");
		dbconn = PropertiesConstants.getString(p, "dbconn",dbconn);
		autoCommit = PropertiesConstants.getBoolean(p, "autoCommit",autoCommit);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		ConnectionPool pool = DbcpSource.getPool(dbcpId);
		if (pool == null) {
			logger.error("Can't get connection pool by dbcpId:" + dbcpId + ", The database connection pool is null!");
			return ;
		}
		Connection conn = pool.getConnection();
		if (conn == null) {
			logger.error("The database connection is null!");
			return ;
		}
		
		boolean hasError = false;
		try {
			conn.setAutoCommit(autoCommit);
			ctx.setObject(dbconn, conn);			
			super.onExecute(root, current, ctx, watcher);
		} catch (SQLException ex) {
			hasError = true;
			throw new BaseException("core.sqlError",ex.getMessage());
		} finally {
			ctx.removeObject(dbconn);
			pool.recycle(conn, hasError);
		}		

	}

}
