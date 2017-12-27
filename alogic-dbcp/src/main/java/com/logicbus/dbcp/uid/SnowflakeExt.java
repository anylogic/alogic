package com.logicbus.dbcp.uid;

import java.sql.Connection;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.uid.impl.Snowflake;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.sql.DBTools;

/**
 * 扩展snowflake算法
 * 
 * <p>在snowflake算法上加强机器id的管理，通过数据库表来维护机器id的唯一。
 * 
 * @author yyduan
 * @since 1.6.11.5
 */
public class SnowflakeExt extends Snowflake{
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		pId = loadPid(
				PropertiesConstants.getString(p,"dbcpId","default"),
				PropertiesConstants.getString(p,"client", "${server.ip}:${server.port}")
				);
	}

	/**
	 * 装入进程id
	 * @param dbcpId
	 */
	protected long loadPid(String dbcpId,String client) {
		ConnectionPool pool = DbcpSource.getPool(dbcpId);
		if (pool == null){
			LOG.error("Can not find the dbcp pool:" + dbcpId);
			return 0;
		}
		
		Connection conn = null;
		boolean error = false;
		try {
			conn = pool.getConnection();
			if (conn != null){
				long pid = DBTools.selectAsLong(conn, "select pid from util_snowflake where client = ?", -1, client);
				if (pid < 0){
					//不存在
					DBTools.setAutoCommit(conn, false);
					try {
						DBTools.insert(conn, "insert into util_snowflake(client,create_date,update_date) values(?,now(),now())", client);
						pid = DBTools.selectAsLong(conn, "select last_insert_id() ;", -1);
						return pid <= 0 ? 0:pid;
					}finally{
						DBTools.commit(conn);
					}
				}
			}else{
				LOG.error("Can not get a db connection from pool + " + dbcpId);
			}
		}catch (BaseException ex){
			error = true;
			LOG.error("Error when load from db:" + dbcpId);
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}finally{
			pool.recycle(conn, error);
		}		
		return 0;
	}
}
