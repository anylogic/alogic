package com.logicbus.dbcp.core;

import java.sql.Connection;

import com.anysoft.metrics.core.MetricsReportable;
import com.anysoft.util.Reportable;


/**
 * 数据库连接池
 * 
 * <br>
 * 管理数据库连接
 * 
 * @author duanyy
 * 
 * @version 1.2.9 [20141016 duanyy]
 * - 重写dbcp
 * 
 * @version 1.2.9 [20141017 duanyy]
 * - 增加{link {@link #getConnection()}
 * - 增加{link {@link #getConnection(boolean)}
 * - 增加{link {@link #getConnection(int)}
 * 
 */
public interface ConnectionPool extends Reportable,MetricsReportable,AutoCloseable{
	
	/**
	 * 从连接池中获取数据库连接
	 * 
	 * @param timeout 超时时间 
	 * @param enableRWS 允许读写分离
	 * @return 数据库连接实例
	 * 
	 */
	public Connection getConnection(int timeout,boolean enableRWS);

	/**
	 * 从连接池中获取数据库连接
	 * @param timeout 超时时间
	 * @return
	 */
	public Connection getConnection(int timeout);
	
	/**
	 * 从连接池中获取数据库连接
	 * @param enableRWS 允许读写分离
	 * @return
	 */
	public Connection getConnection(boolean enableRWS);
	
	/**
	 * 从连接池中获取数据库连接
	 * @return
	 */
	public Connection getConnection();
	
	/**
	 * 归还数据库连接
	 * 
	 * @param conn 数据库连接
	 */
	public void recycle(Connection conn);
	
	/**
	 * 获取连接池名称
	 * 
	 * @return
	 */
	public String getName();
}
