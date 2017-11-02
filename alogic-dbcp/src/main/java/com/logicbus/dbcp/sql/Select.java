package com.logicbus.dbcp.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;
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
 * 
 * @version 1.6.2.4 [20150112 duanyy] <br>
 * - 增加RowRenderer支持<br>
 * 
 * @version 1.6.8.3 [20170328 duanyy] <br>
 * - 修正tlog输出，将参数和错误原因分离开来 <br>
 * 
 * @version 1.6.9.2 [20170601 duanyy] <br>
 * - 增加简单的ORM框架 <br>
 * 
 * @version 1.6.9.8 [20170821 duanyy] <br>
 * - 将SQL语句的绑定参数输出到tlog <br>
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
	 * @return 实例
	 * @throws SQLException
	 */
	public Select execute(String sql,Object... params){
		close();
		TraceContext tc = traceEnable()?Tool.start():null;
		boolean error = false;	
		String msg = "ok";
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
			
			rs = stmt.executeQuery();
			return this;
		}
		catch (SQLException ex){
			error = true;
			msg = ExceptionUtils.getStackTrace(ex);
			throw new BaseException("core.sql_error",msg);
		}finally{
			if (traceEnable() && tc != null){
				Tool.end(tc, "DB", "Query", error ? "FAILED":"OK", msg,String.format("[%s]%s",data,sql),0);
			}
		}
	}
	
	/**
	 * 获取查询结果（单返回值）
	 * 
	 * @return 结果值
	 * @throws SQLException
	 */
	public Object single(){
		try {
			if (rs != null && rs.next()){
				return rs.getObject(1);
			}
			return null;
		}
		catch (SQLException ex){
			throw new BaseException("core.sql_error",ExceptionUtils.getStackTrace(ex));
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
	public long singleAsLong(long dftValue){
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
	public String singleAsString(String dftValue){
		Object result = single();
		
		if (result == null){
			return dftValue;
		}
		
		return result.toString();
	}
	
	/**
	 * 获取查询结果(单行返回值)
	 * 
	 * @return 查询结果
	 * @throws SQLException
	 */
	public Map<String,Object> singleRow(){
		return singleRow(null);
	}
	
	/**
	 * 获取查询结果(单行返回值)
	 * 
	 * @return 查询结果
	 * @throws SQLException
	 * @since 1.6.2.3
	 */
	public Map<String,String> singleRowAsString(){
		return singleRowAsString(null);
	}	

	/**
	 * 获取查询结果(单行返回值)
	 * 
	 * @param result
	 * @return 查询结果(单行返回值)
	 * @throws SQLException
	 * @since 1.2.0
	 */
	public Map<String,Object> singleRow(Map<String,Object> result){
		return singleRow(null,result);
	}		
	
	/**
	 * 获取查询结果(单行返回值)
	 * @param result 预创建的结果集
	 * @param renderer 渲染器
	 * @return 查询结果
	 * @throws BaseException
	 * @since 1.6.2.4
	 */
	public Map<String,Object> singleRow(RowRenderer<Object> renderer,Map<String,Object> result){
		try {
			if (rs != null && rs.next()){
				if (renderer == null){
					renderer = new RowRenderer.Default<Object>();
				}
				
				ResultSetMetaData metadata = rs.getMetaData();
				int columnCount = metadata.getColumnCount();
				
				if (result == null){
					result = renderer.newRow(columnCount);
				}
				
				for (int i = 0 ; i < columnCount ; i++){
					Object value = rs.getObject(i+1);
					if (value == null) continue;
					String id = renderer.getColumnId(metadata, i+1);
					if (id == null) continue;
					result.put(id, value);
				}
				return renderer.render(result);
			}
			return null;
		}
		catch (SQLException ex){
			throw new BaseException("core.sql_error",ExceptionUtils.getStackTrace(ex));
		}
	}	
	
	/**
	 * 获取单行结果
	 * @param result
	 * @return 单行结果
	 * @throws BaseException
	 * @since 1.6.2.3
	 */
	public Map<String,String> singleRowAsString(Map<String,String> result){
		return singleRowAsString(null,result);
	}	
	
	/**
	 * 获取单行结果
	 * @param result 预创建的结果集
	 * @param renderer 渲染器
	 * @return 单行结果
	 * @throws BaseException
	 */
	public Map<String,String> singleRowAsString(RowRenderer<String> renderer,Map<String,String> result){
		try {
			if (rs != null && rs.next()){
				if (renderer == null){
					renderer = new RowRenderer.Default<String>();
				}

				ResultSetMetaData metadata = rs.getMetaData();
				int columnCount = metadata.getColumnCount();
				
				if (result == null){
					result = renderer.newRow(columnCount);
				}
				
				for (int i = 0 ; i < columnCount ; i++){
					Object value = rs.getObject(i+1);
					if (value == null)continue;
					String id = renderer.getColumnId(metadata,i+1);
					if (id == null) continue;
					result.put(id, value.toString());
				}
				
				return renderer.render(result);
			}
			return null;
		}
		catch (SQLException ex){
			throw new BaseException("core.sql_error",ExceptionUtils.getStackTrace(ex));
		}
	}	
	
	/**
	 * 输出结
	 * @param result 结果数组
	 * @param adapter 对象映射适配器
	 */
	public <T> void result(List<T> result,ObjectMappingAdapter<T> adapter){
		if (rs == null || adapter == null){
			return ;
		}
		try{
			while (rs.next()){
				adapter.rowFetched(rs, result);
			}
		}
		catch (SQLException ex){
			throw new BaseException("core.sql_error",ExceptionUtils.getStackTrace(ex));
		}		
	}
	
	public void result(RowListener<Object> rowListener){
		if (rs == null || rowListener == null){
			return ;
		}
		try{
			ResultSetMetaData metadata = rs.getMetaData();
			int columnCount = metadata.getColumnCount();
			while (rs.next()){
				Object cookies = rowListener.rowStart(columnCount);
				
				for (int i = 0 ; i < columnCount ; i++){			
					Object value = rs.getObject(i + 1);
					if (value != null){
						rowListener.columnFound(
								cookies,
								i + 1, 
								metadata, 
								value
								);
					}
				}
				
				rowListener.rowEnd(cookies);
			}
		}
		catch (SQLException ex){
			throw new BaseException("core.sql_error",ExceptionUtils.getStackTrace(ex));
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
	public void resultAsString(RowListener<String> rowListener){
		if (rs == null || rowListener == null){
			return ;
		}
		try{
			ResultSetMetaData metadata = rs.getMetaData();
			int columnCount = metadata.getColumnCount();
			while (rs.next()){
				Object cookies = rowListener.rowStart(columnCount);
				
				for (int i = 0 ; i < columnCount ; i++){			
					Object value = rs.getObject(i + 1);
					if (value != null){
						rowListener.columnFound(
								cookies,
								i + 1, 
								metadata, 
								value.toString()
								);
					}
				}
				
				rowListener.rowEnd(cookies);
			}
		}
		catch (SQLException ex){
			throw new BaseException("core.sql_error",ExceptionUtils.getStackTrace(ex));
		}
	}
	
	/**
	 * 获取查询结果
	 * 
	 * <p>查询结果通过列表返回，可直接作为JSON数据
	 * @return 查询结果
	 * @throws SQLException
	 */
	public List<Map<String,Object>> result(){
		RowListener.Default<Object> data = new RowListener.Default<Object>();
		result(data);
		return data.getResult();
	}
	
	public List<Map<String,Object>> result(RowRenderer<Object> renderer){
		RowListener.Default<Object> data = new RowListener.Default<Object>(renderer);
		result(data);
		return data.getResult();
	}
	
	public List<Map<String,String>> resultAsString(){
		RowListener.Default<String> data = new RowListener.Default<String>();
		resultAsString(data);
		return data.getResult();
	}
	
	public List<Map<String,String>> resultAsString(RowRenderer<String> renderer){
		RowListener.Default<String> data = new RowListener.Default<String>(renderer);
		resultAsString(data);
		return data.getResult();
	}
	
	public void close() throws BaseException {
		close(rs,stmt);
	}	
}
