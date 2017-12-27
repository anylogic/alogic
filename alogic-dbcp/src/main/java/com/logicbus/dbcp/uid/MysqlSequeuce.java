package com.logicbus.dbcp.uid;

import java.sql.Connection;

import com.alogic.uid.IdGenerator;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.sql.DBTools;

/**
 * 基于mysql表模型的序列生成器
 * @author yyduan
 * @since 1.6.11.5
 */
public class MysqlSequeuce extends IdGenerator.Prepare {
	
	/**
	 * 数据库连接池id
	 */
	protected String dbcpId = "default";
	
	/**
	 * 客户端
	 */
	protected String client = "unknown";
	
	/**
	 * 序列id
	 */
	protected String id = "default";
	
	@Override
	public void configure(Properties p) {
		
		dbcpId = PropertiesConstants.getString(p,"dbcpId",dbcpId);
		client = PropertiesConstants.getString(p,"client",client);
		id = PropertiesConstants.getString(p, "id", id);
		super.configure(p);
	}

	@Override
	public void onPrepare(long current, long capacity) {
		ConnectionPool pool = DbcpSource.getPool(dbcpId);
		if (pool == null){
			throw new BaseException("core.e1003","Can not find the dbcp pool:" + dbcpId);
		}
		
		Connection conn = null;
		boolean error = false;
		try {
			conn = pool.getConnection();
			if (conn != null){
				DBTools.setAutoCommit(conn, false);
				try {
					long currentValue = DBTools.selectAsLong(conn,
							"select a.current_value from util_seq a where a.seq_id=? for update",-1,id);
					if (currentValue < 0){
						throw new BaseException("core.e1003","Can not find the sequece :" + id);
					}
					
					DBTools.update(conn, 
							"update util_seq a set a.current_value=? where a.seq_id=?",
							currentValue + capacity,
							id);
					DBTools.insert(conn, 
							"insert into util_seq_log(seq_id,start,end,update_date,client)values(?,?,?,now(),?)",
							id,
							currentValue,
							currentValue + capacity,
							client);
					
					DBTools.commit(conn);
					
					doPrepare(currentValue, currentValue + capacity);
				}catch (Exception ex){
					DBTools.rollback(conn);
				}				
			}else{
				throw new BaseException("core.e1013","Can not get connection from pool:" + dbcpId);
			}
		}catch (BaseException ex){
			error = true;
			throw ex;
		}finally{
			pool.recycle(conn, error);
		}		
	}

}
