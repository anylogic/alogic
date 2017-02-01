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
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class MySQLSequence extends SequenceGenerator.Abstract{
	protected int stringWidth = 20;
	protected String dbcpId = "mall";
	protected String client = "unknown";
	
	protected String sqlQueryCurrentValue = "select a.current_value from util_seq a where a.seq_id=? for update";
	protected String sqlUpdateCurrentValue = "update util_seq a set a.current_value=? where a.seq_id=?";
	protected String sqlInsertLog = "insert into util_seq_log(seq_id,start,end,update_date,client)values(?,?,?,now(),?)";
	
	@Override
	public String nextString() {
		return randomString(stringWidth);
	}

	@Override
	public void onMore(long current, long capacity) {
		long currentValue = current;
		ConnectionPool pool = getConnectionPool();
		Connection conn = pool.getConnection();
		try {
			conn.setAutoCommit(false);
			String id = id();
			
			// 从数据库中查出当前值
			currentValue = DBTools.selectAsLong(conn,sqlQueryCurrentValue,10000,id);
			DBTools.update(conn, sqlUpdateCurrentValue,String.valueOf(currentValue + capacity),id);
			DBTools.insert(conn, sqlInsertLog,id,currentValue,currentValue + capacity,client);

			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			LOG.error("SQL Error:" + e.getMessage());
			try {
				conn.rollback();
				conn.setAutoCommit(true);
			}catch (Exception ex){
				LOG.error("SQL Error:" + ex.getMessage());
			}
		} finally {
			pool.recycle(conn);
		}

		setRange(currentValue, currentValue + capacity);
	}

	@Override
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
}
