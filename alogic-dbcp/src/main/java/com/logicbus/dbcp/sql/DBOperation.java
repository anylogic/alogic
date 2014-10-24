package com.logicbus.dbcp.sql;

import java.sql.Connection;

/**
 * 数据库操作
 * 
 * @author duanyy
 * @since 1.2.5
 * 
 */
abstract public class DBOperation implements AutoCloseable{
	
	protected Connection conn = null;
	protected DBOperation(Connection _conn){
		conn = _conn;
	}

	/**
	 * 关闭所有句柄
	 * @param autoCloseables 句柄列表
	 */
	public static void close(AutoCloseable... autoCloseables){
		for (AutoCloseable c:autoCloseables){
			if (null != c){
				try{
					c.close();
				}catch (Exception ex){
					
				}
			}
		}
	}	
}
