package com.alogic.idu.util;

import java.sql.Connection;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.sql.DBTools;
import com.logicbus.models.servant.ServiceDescription;

/**
 * Idu Base
 * 
 * @author duanyy
 * @since 1.6.4.6
 * 
 */
abstract public class IDUBase extends Base {
	/**
	 * 数据库连接池ID,可通过参数dbSource配置,缺省为itportal
	 */
	protected String dbcpId = "itportal";
	
	/**
	 * 记录业务日志的SQL语句,可通过参数log.sql配置,缺省为
	 * insert into util_audit_log(operator,client,entity_id,entity_type,operation,note) values(?,?,?,?,?,?)
	 */
	protected String logSql = "insert into util_audit_log(operator,client,entity_id,entity_type,operation,note) values(?,?,?,?,?,?)";
	
	/**
	 * 是否记录日志,可通过参数log.on配置，缺省为true
	 */
	protected boolean logOn = false;
	
	/**
	 * 日志中实体ID的类型，可通过参数log.type配置，缺省为Null
	 */
	protected String logType = "Null";
	
	/**
	 * 日志中的操作代码,可通过参数log.operation配置，缺省为服务名
	 */
	protected String logOperation = "Unknown";
	
	/**
	 * 日志中的记录内容，可通过log.content配置，缺省为空
	 */
	protected String logContent = "";	
	
	/**
	 * 是否支持事务,可通过transactionSupport配置，缺省为true
	 */
	protected boolean transactionSupport = true;

	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) {
		Properties p = sd.getProperties();

		dbcpId = PropertiesConstants.getString(p, "dbSource", dbcpId,true);	
		transactionSupport =  PropertiesConstants.getBoolean(p,"transactionSupport",logOn,transactionSupport);
		logOn = PropertiesConstants.getBoolean(p,"log.on",logOn,true);
		logSql = PropertiesConstants.getString(p, "log.sql", logSql,true);	
		logType = PropertiesConstants.getString(p, "log.type", logType,true);	
		logOperation = PropertiesConstants.getString(p, "log.operation", sd.getServiceID(),true);	
		logContent = p.GetValue("log.content", logContent, false, true);	
		
		super.onCreate(sd);
	}
	
	protected int onJson(Context ctx,JsonMessage msg)  {
		ConnectionPool pool = getConnectionPool();
		Connection conn = pool.getConnection();
		boolean hasError = false;
		boolean autoCommit = DBTools.getAutoCommit(conn);
		try {
			if (transactionSupport){
				DBTools.setAutoCommit(conn, false);
			}
			doIt(ctx, msg, conn);
			if (transactionSupport){
				DBTools.commit(conn);
			}
		} catch (BaseException ex){
			if (ex.getCode().equals("core.e1300")){
				hasError = true;
			}
			if (transactionSupport){
				DBTools.rollback(conn);
			}
			throw ex;
		}finally {
			DBTools.setAutoCommit(conn, autoCommit);
			pool.recycle(conn,hasError);
		}	
		return 0;
	}
	
	abstract protected void doIt(Context ctx, JsonMessage msg, Connection conn)
			;	
	
	protected ConnectionPool getConnectionPool() {
		ConnectionPool pool = DbcpSource.getPool(dbcpId);
		if (pool == null) {
			throw new ServantException("core.e1003",
					"Can not get a connection pool named " + dbcpId);
		}
		return pool;
	}

	protected Connection getConnection(ConnectionPool pool) {
		Connection conn = pool.getConnection(3000, true);
		if (conn == null) {
			throw new ServantException("core.e1013",
					"Can not get a connection from pool named " + dbcpId);
		}
		return conn;
	}	
		
	/**
	 * 记录业务日志
	 * @param conn 数据库连接
 	 * @param operator 操作者
	 * @param client 客户端
	 * @param id 对象ID
	 */
	protected void bizLog(Connection conn, String operator, String client,
			String id) {
		if (logOn){
			DBTools.insert(conn, logSql, operator, client, id, logType, logOperation,
					logContent);
		}
	}	
	
	/**
	 * 记录业务日志
	 * @param conn 数据库连接
 	 * @param operator 操作者
	 * @param client 客户端
	 * @param id 对象ID
	 * @param ctx 调用上下文
	 */
	protected void bizLog(Connection conn, String operator, String client,
			String id,Context ctx) {
		if (logOn){
			String content = ctx.transform(logContent);
			DBTools.insert(conn, logSql, operator, client, id, logType, logOperation,
					content);
		}
	}	
	
	/**
	 * 记录业务日志
	 * @param conn 数据库连接
	 * @param operator 操作者
	 * @param client 客户端
	 * @param id 对象id
	 * @param content 日志内容
	 */
	protected void bizLog(Connection conn, String operator, String client,
			String id,String content){
		if (logOn){
			DBTools.insert(conn, logSql, operator, client, id, logType, logOperation,
					content);
		}
	}	
}
