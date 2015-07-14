package com.logicbus.dbcp.sql;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * DB相关的一些工具
 * 
 * @author duanyy
 * @since 1.6.3.30
 */
public class DBTools {
	
	/**
	 * 通过SQL语句查询单个值
	 * 
	 * @param conn 数据库连接
	 * @param sql SQL语句
	 * @param args SQL语句的参数集
	 * @return 查询出的对象
	 */
	public static Object selectAsObject(Connection conn, String sql,
			Object... args) {
		Select selector = new Select(conn);
		try {
			selector.execute(sql, args);
			return selector.single();
		} finally {
			Select.close(selector);
		}
	}
	
	/**
	 * 通过SQL语句查询单个值 
	 * @param conn 数据库连接
	 * @param sql SQL语句
	 * @param args SQL语句的参数集
	 * @return 查询出的对象
	 */
	public static String selectAsString(Connection conn, String sql,
			Object... args) {
		Object obj = selectAsObject(conn, sql, args);
		if (obj != null) {
			return obj.toString();
		}
		return null;
	}
	
	/**
	 * 通过SQL语句查询单个值（Long）
	 * @param conn 数据库连接
	 * @param sql SQL语句
	 * @param dftValue 缺省值
	 * @param args SQL语句的参数集
	 * @return 结果值(Long)
	 */
	public static Long selectAsLong(Connection conn, String sql, long dftValue,Object... args) {
		String s = selectAsString(conn, sql, args);
		long value = dftValue;
		if (isNotNull(s)) {
			try {
				value = Long.valueOf(s);
			}catch (Exception ex){
			}
		}
		return value;
	}

	/**
	 * 通过SQL语句查询单个值（Integer）
	 * @param conn 数据库连接
	 * @param sql SQL语句
	 * @param dftValue 缺省值
	 * @param args SQL语句的参数集
	 * @return 结果值(Integer)
	 */	
	public static Integer selectAsInt(Connection conn, String sql, int dftValue,Object... args) {
		String s = selectAsString(conn, sql, args);
		int value = dftValue;
		if (isNotNull(s)) {
			try {
				value = Integer.valueOf(s);
			}catch (Exception ex){
			}
		}
		return value;
	}
	
	/**
	 * 执行Update语句
	 * @param conn 数据库连接
	 * @param sql SQL语句
	 * @param args SQL语句的参数
	 * @return 影响记录行数
	 */
	public static int update(Connection conn, String sql, Object... args) {
		Update update = new Update(conn);
		try {
			return update.execute(sql, args);
		} finally {
			Update.close(update);
		}
	}

	/**
	 * 执行Insert语句
	 * @param conn 数据库连接
	 * @param sql SQL语句
	 * @param args SQL语句的参数
	 * @return 影响记录行数
	 */
	public static int insert(Connection conn, String sql, Object... args) {
		return update(conn, sql, args);
	}

	/**
	 * 执行Delete语句
	 * @param conn 数据库连接
	 * @param sql SQL语句
	 * @param args SQL语句的参数
	 * @return 影响记录行数
	 */
	public static int delete(Connection conn, String sql, Object... args) {
		return update(conn, sql, args);
	}
	
	/**
	 * Select单行数据
	 * 
	 * @param conn 数据库连接
	 * @param sql SQL语句
	 * @param args SQL语句的参数
	 * @return 单行数据
	 */
	public static Map<String, String> select(Connection conn, String sql,
			Object... args) {
		Select selector = new Select(conn);
		try {
			selector.execute(sql, args);
			return selector.singleRowAsString();
		} finally {
			Select.close(selector);
		}
	}

	/**
	 * Select单行数据
	 * 
	 * @param conn 数据库连接
	 * @param sql SQL语句
	 * @param renderer 行数据渲染器
	 * @param args SQL语句的参数
	 * @return 单行数据
	 */	
	public static Map<String, String> select(Connection conn, String sql,
			RowRenderer<String> renderer, Object... args) {
		Select selector = new Select(conn);
		try {
			selector.execute(sql, args);
			if (renderer != null) {
				return selector.singleRowAsString(renderer, null);
			}
			return selector.singleRowAsString();
		} finally {
			Select.close(selector);
		}
	}

	/**
	 * Select多行数据
	 * 
	 * @param conn 数据库连接
	 * @param sql SQL语句
	 * @param args SQL语句的参数
	 * @return 多行数据
	 */	
	public static List<Map<String, Object>> listAsObject(Connection conn,
			String sql, Object... args) {
		return listAsObject(conn, sql, null, args);
	}
	/**
	 * Select多行数据
	 * 
	 * @param conn 数据库连接
	 * @param sql SQL语句
	 * @param renderer 行数据渲染器
	 * @param args SQL语句的参数
	 * @return 多行数据
	 */	
	public static List<Map<String, Object>> listAsObject(Connection conn,
			String sql, RowRenderer<Object> renderer, Object... args) {
		Select selector = new Select(conn);
		try {
			selector.execute(sql, args);
			if (renderer != null) {
				return selector.result(renderer);
			}
			return selector.result();
		} finally {
			Select.close(selector);
		}
	}
	/**
	 * Select多行数据
	 * 
	 * @param conn 数据库连接
	 * @param sql SQL语句
	 * @param args SQL语句的参数
	 * @return 多行数据
	 */	
	public static List<Map<String, String>> list(Connection conn, String sql,
			Object... args) {
		return list(conn, sql, null, args);
	}
	
	/**
	 * Select多行数据
	 * 
	 * @param conn 数据库连接
	 * @param sql SQL语句
	 * @param renderer 行数据渲染器
	 * @param args SQL语句的参数
	 * @return 多行数据
	 */	
	public static List<Map<String, String>> list(Connection conn, String sql,
			RowRenderer<String> renderer, Object... args) {
		Select selector = new Select(conn);
		try {
			selector.execute(sql, args);
			if (renderer != null) {
				return selector.resultAsString(renderer);
			}
			return selector.resultAsString();
		} finally {
			Select.close(selector);
		}
	}	
	
	private static boolean isNotNull(String value){
		return value != null && value.length() > 0;
	}
}
