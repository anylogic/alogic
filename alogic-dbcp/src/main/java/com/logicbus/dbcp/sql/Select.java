package com.logicbus.dbcp.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anysoft.util.BaseException;

/**
 * 查询语句操作类
 * 
 * @author duanyy
 * @since 1.2.5
 * 
 * @version 1.3.0.2 [20141106 duanyy] <br>
 * - List,Map等类采用泛型 <br>
 * 
 * @version 1.6.2.3 [20150108 duanyy] <br>
 * - 增加{@link #singleAsLong(long)} <br>
 * - 增加{@link #singleAsString(String)} <br>
 * - 增加{@link #singleRowAsString()} <br>
 * - 增加{@link #singleRowAsString(Map)} <br>
 */
public class Select extends DBOperation {

	public Select(Connection conn) {
		super(conn);
	}

	protected PreparedStatement stmt = null;
	protected ResultSet rs = null;
	
	/**
	 * 执行SQL语句
	 * @param sql SQL语句
	 * @param params 参数列表
	 * @return
	 * @throws SQLException
	 */
	public Select execute(String sql,Object... params) throws BaseException{
		close();
		
		try {
			stmt = conn.prepareStatement(sql);
			
			if (params != null){
				for (int i = 0 ; i < params.length ; i ++){
					stmt.setObject(i + 1, params[i]);
				}
			}
			
			rs = stmt.executeQuery();
			return this;
		}
		catch (SQLException ex){
			throw new BaseException("core.sql_error","Error occurs when executing sql:" + ex.getMessage());
		}
	}
	
	/**
	 * 获取查询结果（单返回值）
	 * 
	 * @return 结果值
	 * @throws SQLException
	 */
	public Object single()throws BaseException{
		try {
			if (rs != null && rs.next()){
				return rs.getObject(1);
			}
			return null;
		}
		catch (SQLException ex){
			throw new BaseException("core.sql_error","Error occurs when executing sql:" + ex.getMessage());
		}		
	}

	/**
	 * 以Long形式获取查询结果（单返回值）
	 * @param dftValue 缺省值
	 * @return 结果值
	 * @throws BaseException
	 * 
	 * @since 1.6.2.3
	 */
	public long singleAsLong(long dftValue)throws BaseException{
		Object result = single();
		
		if (result == null){
			return dftValue;
		}
		
		if (result instanceof Number){
			Number value = (Number) result;
			return value.longValue();
		}
		
		String value = result.toString();
		try{
			return Long.parseLong(value);
		}catch (Exception ex){
			return dftValue;
		}
	}
	
	/**
	 * 以String形式获取查询结果（单返回值）
	 * @param dftValue 缺省值
	 * @return 结果值
	 * @throws BaseException
	 * 
	 * @since 1.6.2.3
	 */
	public String singleAsString(String dftValue)throws BaseException{
		Object result = single();
		
		if (result == null){
			return dftValue;
		}
		
		return result.toString();
	}
	
	/**
	 * 获取查询结果(单行返回值)
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Map<String,Object> singleRow()throws BaseException{
		return singleRow(null);
	}
	
	/**
	 * 获取查询结果(单行返回值)
	 * 
	 * @return
	 * @throws SQLException
	 * s@since 1.6.2.3
	 */
	public Map<String,String> singleRowAsString()throws BaseException{
		return singleRowAsString(null);
	}	

	/**
	 * 获取查询结果(单行返回值)
	 * 
	 * @param result
	 * @return
	 * @throws SQLException
	 * @since 1.2.0
	 */
	public Map<String,Object> singleRow(Map<String,Object> result)throws BaseException{
		try {
			if (rs != null && rs.next()){
				if (result == null)
				result = new HashMap<String,Object>();
				
				ResultSetMetaData metadata = rs.getMetaData();
				int columnCount = metadata.getColumnCount();
				for (int i = 0 ; i < columnCount ; i++){
					Object value = rs.getObject(i+1);
					if (value == null)continue;
					//1.2.0 支持列的别名
					String name = metadata.getColumnLabel(i+1);
					if (name == null){
						name = metadata.getColumnName(i+1);
					}
					result.put(name.toLowerCase(), value);
				}
				
				return result;
			}
			return null;
		}
		catch (SQLException ex){
			throw new BaseException("core.sql_error","Error occurs when executing sql:" + ex.getMessage());
		}
	}		
	
	/**
	 * 获取单行结果
	 * @param result
	 * @return
	 * @throws BaseException
	 * @since 1.6.2.3
	 */
	public Map<String,String> singleRowAsString(Map<String,String> result)throws BaseException{
		try {
			if (rs != null && rs.next()){
				if (result == null)
				result = new HashMap<String,String>();
				
				ResultSetMetaData metadata = rs.getMetaData();
				int columnCount = metadata.getColumnCount();
				for (int i = 0 ; i < columnCount ; i++){
					Object value = rs.getObject(i+1);
					if (value == null)continue;
					//1.2.0 支持列的别名
					String name = metadata.getColumnLabel(i+1);
					if (name == null){
						name = metadata.getColumnName(i+1);
					}
					result.put(name.toLowerCase(), value.toString());
				}
				
				return result;
			}
			return null;
		}
		catch (SQLException ex){
			throw new BaseException("core.sql_error","Error occurs when executing sql:" + ex.getMessage());
		}
	}	
	
	/**
	 * 获取查询结果
	 * 
	 * <p>查询结果通过监听器获取
	 * 
	 * @param rowListener 行监听器
	 * @throws SQLException
	 */
	public void result(RowListener rowListener)throws BaseException{
		if (rs == null || rowListener == null){
			return ;
		}
		try{
			ResultSetMetaData metadata = rs.getMetaData();
			int columnCount = metadata.getColumnCount();
			while (rs.next()){
				Object cookies = rowListener.rowStart(columnCount);
				
				for (int i = 0 ; i < columnCount ; i++){
					//1.2.0 支持列的别名
					String name = metadata.getColumnLabel(i+1);
					if (name == null){
						name = metadata.getColumnName(i+1);
					}					
					rowListener.columnFound(
							cookies,
							i, 
							name.toLowerCase(), 
							rs.getObject(i+1)
							);
				}
				
				rowListener.rowEnd(cookies);
			}
		}
		catch (SQLException ex){
			throw new BaseException("core.sql_error","Error occurs when executing sql:" + ex.getMessage());
		}
	}

	/**
	 * 获取查询结果
	 * 
	 * <p>查询结果通过列表返回，可直接作为JSON数据
	 * @return
	 * @throws SQLException
	 */
	public List<Object> result()throws BaseException{
		InnerRowListner data = new InnerRowListner();
		result(data);
		return data.getResult();
	}
	
	
	public void close() throws BaseException {
		close(stmt,rs);
	}	

	/**
	 * 内置的行数据监听器
	 * 
	 * @author duanyy
	 *
	 */
	public static class InnerRowListner implements RowListener{
		protected ArrayList<Object> result = new ArrayList<Object>();

		public List<Object> getResult(){
			return result;
		}

		public Object rowStart(int column) {
			return new HashMap<String,Object>(5);
		}
		
		public void columnFound(Object cookies,int columnIndex, String name, Object value) {
			if (value != null){
				@SuppressWarnings({ "unchecked", "rawtypes" })
				Map<String, Object> map = (Map)cookies;
				map.put(name, value);
			}
		}
		
		public void rowEnd(Object cookies) {
			result.add(cookies);
		}
		
	}
}
