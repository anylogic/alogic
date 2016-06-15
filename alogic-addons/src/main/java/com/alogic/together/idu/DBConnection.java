package com.alogic.together.idu;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
import com.alogic.together.plugins.Segment;
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
 */
public class DBConnection extends Segment {
	
	protected String dbcpId;
	protected String dbconn = "dbconn";
	
	public DBConnection(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		dbcpId = PropertiesConstants.getString(p,"dbcpId","");
		dbconn = PropertiesConstants.getString(p, "dbconn",dbconn);
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
		boolean autoCommit = true;
		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);

			ctx.setObject(dbconn, conn);
			
			super.onExecute(root, current, ctx, watcher);
			
			conn.commit();
			conn.setAutoCommit(autoCommit);
		} catch (Exception ex) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				hasError = true;
				logger.error(e.getMessage());
			}
			hasError = true;
			if (ex instanceof BaseException){
				throw (BaseException)ex;
			}else{
				throw new BaseException("core.unknown",ex.getMessage());
			}
		} finally {
			ctx.removeObject(dbconn);
			pool.recycle(conn, hasError);
		}		

	}

}
