package com.logicbus.dbcp.sql;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;

/**
 * 数据库操作
 * 
 * @author duanyy
 * @since 1.2.5
 * 
 */
abstract public class DBOperation implements AutoCloseable{
	protected static final Logger LOG = LoggerFactory.getLogger(DBOperation.class);
	protected Connection conn = null;
	protected static boolean traceEnable = false;
	protected DBOperation(Connection _conn){
		conn = _conn;
	}

	public boolean traceEnable(){
		return traceEnable;
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
	
	static {
		Properties p = Settings.get();
		traceEnable = PropertiesConstants.getBoolean(p, "tracer.dbcp.enable", false);
	}
}
