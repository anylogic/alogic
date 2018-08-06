package com.logicbus.dbcp.xscript;

import java.sql.Connection;
import java.sql.SQLException;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;

/**
 * 开启一个数据库连接
 * @since 1.6.10.5
 * 
 * @version 1.6.11.24 [20180323 duanyy]
 * - 增加db-select插件，用于选择一行记录，并插入到变量集 <br>
 * 
 * @version 1.6.11.27 [20180417 duanyy] <br>
 * - 增加db-keyvalues插件 <br>
 * 
 * @version 1.6.11.47 [20180806 duanyy] <br>
 * - 当数据库连接已经存在的时候，直接使用 <br>
 */
public class DBConn extends Segment {
	
	protected String dbcpId;
	protected String dbconn = "dbconn";
	protected boolean autoCommit = true;
	
	public DBConn(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("db",DBConn.class);
		registerModule("db-commit",Commit.class);
		registerModule("db-delete",Delete.class);
		registerModule("db-update",Update.class);
		registerModule("db-list",ListAll.class);
		registerModule("db-rollback",Rollback.class);
		registerModule("db-scan",Scan.class);
		registerModule("db-new",New.class);
		registerModule("db-select",Select.class);
		registerModule("db-query",Query.class);
		registerModule("db-keyvalues",KeyValues.class);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		dbcpId = PropertiesConstants.getString(p,"dbcpId","");
		dbconn = PropertiesConstants.getString(p, "dbconn",dbconn);
		autoCommit = PropertiesConstants.getBoolean(p, "autoCommit",autoCommit);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Connection conn = ctx.getObject(dbconn);
		if (conn == null){
			ConnectionPool pool = DbcpSource.getPool(dbcpId);
			if (pool == null) {
				logger.error("Can't get connection pool by dbcpId:" + dbcpId + ", The database connection pool is null!");
				return ;
			}
			conn = pool.getConnection();
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
				throw new BaseException("core.e1300",ex.getMessage());
			} finally {
				ctx.removeObject(dbconn);
				pool.recycle(conn, hasError);
			}
		}else{
			//连接已经存在
			super.onExecute(root, current, ctx, watcher);
		}		
	}
}
