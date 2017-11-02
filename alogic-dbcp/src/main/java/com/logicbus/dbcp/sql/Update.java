package com.logicbus.dbcp.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;
import com.anysoft.util.BaseException;


/**
 * Update操作
 * 
 * @author duanyy
 * @since 1.2.5
 * 
 * @version 1.3.0.2 [20141106 duanyy] <br>
 * - List,Map等类采用泛型 <br>
 * 
 * @version 1.6.8.3 [20170328 duanyy] <br>
 * - 修正tlog输出，将参数和错误原因分离开来 <br>
 * 
 * @version 1.6.9.1 [20170516 duanyy] <br>
 * - 优化异常信息 <br>
 * 
 * @version 1.6.9.8 [20170821 duanyy] <br>
 * - 将SQL语句的绑定参数输出到tlog <br>
 */
public class Update extends DBOperation {

	/**
	 * 通过一个Connection构造
	 * @param _conn a valid connection
	 */
	public Update(Connection _conn) {
		super(_conn);
	}

	
	public void close() throws Exception {
		
	}

	/**
	 * 执行单个SQL语句
	 * @param sql 
	 * @param params 参数列表
	 * @return 结果
	 * @throws SQLException
	 */
	public int execute(String sql,Object...params) throws BaseException{
		PreparedStatement stmt = null;
		TraceContext tc = traceEnable()?Tool.start():null;
		boolean error = false;
		String msg = "OK";
		StringBuffer data = new StringBuffer();
		try {
			stmt = conn.prepareStatement(sql);
			
			if (params != null){
				for (int i = 0 ; i < params.length ; i ++){
					if (i != 0){
						data.append(",");
					}
					data.append(params[i]);					
					stmt.setObject(i + 1, params[i]);
				}
			}
			
			return stmt.executeUpdate();
		}catch (SQLException ex){
			error = true;
			msg = ExceptionUtils.getStackTrace(ex);
			throw new BaseException("core.sql_error",msg);
		}
		finally{
			close(stmt);
			if (traceEnable() && tc != null){
				Tool.end(tc, "DB", "Update", error ? "FAILED":"OK", msg ,String.format("[%s]%s",data.toString(),sql), 0);
			}
		}
	}
	
	/**
	 * 执行多个SQL语句
	 * @param sqls
	 * @return 执行结果
	 * @throws SQLException
	 */
	public int[] executeBatch(String...sqls) throws BaseException{
		Statement stmt = null;
		TraceContext tc = traceEnable()?Tool.start():null;
		boolean error = false;
		String msg = "OK";
		try {
			stmt = conn.createStatement();
			
			for (String sql:sqls){
				stmt.addBatch(sql);
			}			
			return stmt.executeBatch();
		}catch (SQLException ex){
			error = true;
			msg = ExceptionUtils.getStackTrace(ex);
			throw new BaseException("core.sql_error",msg);
		}
		finally{
			close(stmt);
			if (traceEnable() && tc != null){
				Tool.end(tc, "DB", "Update", error ? "FAILED":"OK", msg,sqls.toString(),0);
			}
		}
	}
}
