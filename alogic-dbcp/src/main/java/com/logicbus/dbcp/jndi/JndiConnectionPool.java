package com.logicbus.dbcp.jndi;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.metrics.stream.MetricsCollector;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.sql.DBTools;
import com.logicbus.dbcp.util.ConnectionPoolStat;

/**
 * 基于JNDI DataSource的连接池
 * 
 * @author duanyy
 * 
 * @since 1.2.9
 * 
 * @version 1.2.9.1 [20141017 duanyy]
 * - ConnectionPoolStat模型更新
 * 
 * @version 1.6.3.11 [20150402 duanyy] <br>
 * - 增加{@link #recycle(Connection, boolean)},获取客户的使用反馈,以便连接池的处理 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class JndiConnectionPool implements ConnectionPool {
	protected static final Logger logger = LoggerFactory.getLogger(JndiConnectionPool.class);
	protected String name;
	protected DataSource datasource = null;
	protected ConnectionPoolStat stat = null;
	public JndiConnectionPool(String _name,DataSource _ds){
		name = _name;
		datasource = _ds;
	}
	
	
	public Connection getConnection(int timeout,boolean enableRWS) {
		Connection conn = null;
		if (datasource != null){
			long start = System.currentTimeMillis();
			try {
				return datasource.getConnection();
			} catch (SQLException e) {
				logger.error("Error when getting jdbc connection from datasource:" + name,e);
			}finally{
				if (stat != null){
					stat.count(System.currentTimeMillis() - start, conn == null);
				}
			}
		}
		return conn;
	}

	
	public Connection getConnection(int timeout) {
		return getConnection(3000,false);
	}

	
	public Connection getConnection(boolean enableRWS) {
		return getConnection(3000,enableRWS);
	}

	
	public Connection getConnection() {
		return getConnection(3000,false);
	}
	
	
	public void recycle(Connection conn) {
		DBTools.close(conn);
	}
	
	public void recycle(Connection conn, boolean hasError) {
		DBTools.close(conn);
	}
	
	public String getName() {
		return name;
	}

	
	public void report(Element root) {
		if (root != null){
			root.setAttribute("module", JndiConnectionPool.class.getName());
			
			Document doc = root.getOwnerDocument();
			// runtime
			{
				Element _runtime = doc.createElement("runtime");
				
				if (stat != null){
					Element _stat = doc.createElement("stat");
					stat.report(_stat);			
					_runtime.appendChild(_stat);
				}
				
				root.appendChild(_runtime);
			}
		}
	}

	
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("module", JndiConnectionPool.class.getName());
			
			// runtime
			{
				Map<String,Object> _runtime = new HashMap<String,Object>();
				
				if (stat != null){
					Map<String,Object> _stat = new HashMap<String,Object>();
					stat.report(_stat);			
					_runtime.put("stat", _stat);
				}
				json.put("runtime",_runtime);
			}
		}
	}

	
	public void report(MetricsCollector collector) {
		
	}

	
	public void close() throws Exception {
	}

}
