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
 * @version 1.2.9 [20141016 duanyy] <br>
 * - 重写dbcp <br>
 * 
 * @version 1.2.9 [20141017 duanyy] <br>
 * - 增加{@link #getConnection()} <br>
 * - 增加{@link #getConnection(boolean)} <br>
 * - 增加{@link #getConnection(int)} <br>
 * 
 * @version 1.6.3.11 [20150402 duanyy] <br>
 * - 增加{@link #recycle(Connection, boolean)},获取客户的使用反馈,以便连接池的处理 <br>
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
	 * 归还数据库连接，并告诉框架本次连接是否发生过错误
	 * 
	 * @param conn 数据库连接
	 * @param hasError 如果发生过错误为true，反之为false
	 */
	public void recycle(Connection conn,boolean hasError);
	
	/**
	 * 获取连接池名称
	 * 
	 * @return
	 */
	public String getName();
}
