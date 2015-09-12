package com.alogic.idu.seq;

import java.sql.Connection;
import java.sql.SQLException;
import org.w3c.dom.Element;
import com.alogic.sequence.core.SequenceGenerator;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.sql.DBTools;

/**
 * 基于MySQL的序列号生成器
 * 
 * @author duanyy
 * @since 1.6.4.6
 * 
 */
public class MySQLSequence extends SequenceGenerator.Abstract{

	public String nextString() {
		return randomString(stringWidth);
	}

	public void onMore(long current, long capacity) {
		long _current = current;
		ConnectionPool pool = getConnectionPool();
		Connection conn = pool.getConnection();
		try {
			conn.setAutoCommit(false);
			String _id = id();
			
			// 从数据库中查出当前值
			_current = DBTools.selectAsLong(conn,sqlQueryCurrentValue,10000,_id);
			DBTools.update(conn, sqlUpdateCurrentValue,String.valueOf(_current + capacity),_id);
			DBTools.insert(conn, sqlInsertLog,_id,_current,_current + capacity,client);

			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
				conn.setAutoCommit(true);
			}catch (Exception ex){
				ex.printStackTrace();
			}
		} finally {
			pool.recycle(conn);
		}

		setRange(_current, _current + capacity);
	}

	public void onConfigure(Element e, Properties p) {
		stringWidth = PropertiesConstants.getInt(p,"stringWidth", stringWidth);
		dbcpId = PropertiesConstants.getString(p,"dbcp", dbcpId);
		client = Settings.get().transform("${server.host}:${server.port}");
		sqlQueryCurrentValue = PropertiesConstants.getString(p,"sql.QueryCurrentValue",sqlQueryCurrentValue);
		sqlUpdateCurrentValue = PropertiesConstants.getString(p,"sql.UpdateCurrentValue",sqlUpdateCurrentValue);
		sqlInsertLog = PropertiesConstants.getString(p,"sql.InsertLog",sqlInsertLog);
	}
	
	protected ConnectionPool getConnectionPool(){
		return DbcpSource.getPool(dbcpId);
	}

	protected int stringWidth = 20;
	protected String dbcpId = "mall";
	protected String client = "unknown";
	
	protected String sqlQueryCurrentValue = "select a.current_value from util_seq a where a.seq_id=? for update";
	protected String sqlUpdateCurrentValue = "update util_seq a set a.current_value=? where a.seq_id=?";
	protected String sqlInsertLog = "insert into util_seq_log(seq_id,start,end,update_date,client)values(?,?,?,now(),?)";
}
