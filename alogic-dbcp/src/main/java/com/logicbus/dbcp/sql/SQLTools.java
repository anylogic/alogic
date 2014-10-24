package com.logicbus.dbcp.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.anysoft.util.BaseException;


/**
 * SQL语句相关的工具
 * 
 * @author duanyy
 * @since 1.2.5
 * 
 */
public class SQLTools {
	/**
	 * 批量执行DML语句
	 * @param conn 数据库连接
	 * @param sqls SQL语句连接
	 * @return 每个SQL执行的状态
	 * @throws ServantException 当SQL语句执行错误时，抛出此异常
	 */
	public static int [] executeBatch(Connection conn,String [] sqls) throws BaseException{
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			
			for (int i = 0 ; i< sqls.length ; i ++){
				stmt.addBatch(sqls[i]);
			}
			
			return stmt.executeBatch();
		}catch (SQLException ex){
			throw new BaseException("core.sql_error","SQL batch execute error:" + ex.getMessage());
		}finally {
			close(stmt);
		}
	}

	/**
	 * 执行单个SQL语句
	 * @param conn 数据库联接
	 * @param sql SQL语句
	 * @return SQL执行状态
	 * @throws ServantException
	 */
	public static int executeBatch(Connection conn,String sql) throws BaseException{
		Statement stmt = null;
		try {
			stmt = conn.createStatement();		
			return stmt.executeUpdate(sql);
		}catch (SQLException ex){
			throw new BaseException("core.sql_error","SQL batch execute error:" + ex.getMessage());
		}finally {
			close(stmt);
		}
	}
	
	/**
	 * 创建Statement
	 * @param conn 数据库连接
	 * @return Statement
	 * @throws ServantException
	 */
	public static Statement createStatement(Connection conn) throws BaseException{
		try {
			return conn.createStatement();
		}catch (SQLException ex){
			throw new BaseException("core.sql_error","Statement create error:" + ex.getMessage());
		}
	}
	public static PreparedStatement prepareStatement(Connection conn,String sql) throws BaseException{
		try {
			return conn.prepareStatement(sql);
		}catch (SQLException ex){
			throw new BaseException("core.sql_error","Statement create error:" + ex.getMessage());
		}
	}	
	
	public static CallableStatement prepareCall(Connection conn,String sql) throws BaseException{
		try {
			return conn.prepareCall(sql);
		}catch (SQLException ex){
			throw new BaseException("core.sql_error","Statement create error:" + ex.getMessage());
		}
	}	
	/**
	 * 执行查询SQL语句并返回数据集
	 * @param stmt Statement
	 * @param sql SQL语句
	 * @return 数据集
	 * @throws ServantException
	 */
	public static ResultSet executeQuery(Statement stmt, String sql) throws BaseException {
		try {
			return stmt.executeQuery(sql);
		} catch (SQLException ex) {
			throw new BaseException("core.sql_error",
					"SQL execute error:" + ex.getMessage());
		}
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
	
	/**
	 * 提交事务
	 * @param conn 连接
	 * @throws ServantException
	 */
	public static void commit(Connection conn) throws BaseException{
		try {
			conn.commit();
		} catch (SQLException ex) {
			throw new BaseException("core.sql_error",ex.getMessage());
		}
	}
	
	/**
	 * 回滚事务
	 * @param conn
	 * @throws ServantException
	 */
	public static void rollback(Connection conn) throws BaseException{
		try {
			conn.rollback();
		} catch (SQLException ex) {
			throw new BaseException("core.sql_error",ex.getMessage());
		}
	}	

}
