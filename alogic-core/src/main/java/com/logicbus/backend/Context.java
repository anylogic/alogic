package com.logicbus.backend;

import java.sql.Connection;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.KeyGen;

/**
 * 服务访问的上下文
 * 
 * <p>
 * 记录了本次访问的一些上下文信息，例如服务参数、客户端IP等
 * 
 * @author duanyy
 * 
 * @version 1.0.5 [20140412 duanyy]
 * - 改进消息传递模型
 * 
 * @version 1.0.7 [20140418 duanyy]
 * - 增加生成全局序列号功能
 * 
 */
abstract public class Context extends DefaultProperties{
	
	/**
	 * 按照全局序列号构造上下文
	 * 
	 * @param _globalSerial
	 * @since 1.0.7
	 */
	protected Context(String _globalSerial){
		globalSerial = _globalSerial;
	}
	
	/**
	 * a db connection
	 */
	private Connection m_conn;
	
	
	/**
	 * to get the client ip
	 * @return client ip
	 */
	abstract public String getClientIp();
	
	
	/**
	 * 获取主机信息
	 * @return
	 */
	abstract public String getHost();
	
	/**
	 * 获取请求路径
	 * @return
	 */
	abstract public String getRequestURI();
	
	/**
	 * to get the db connection
	 * @return db connection
	 */
	public Connection getConnection(){return m_conn;}
	
	/**
	 * to set the db connection
	 * @param conn connection
	 */
	public void setConnection(Connection conn){m_conn = conn;}
	
	/**
	 * 全局序列号
	 * 
	 * @since 1.0.7
	 */
	private String globalSerial = null;
	
	/**
	 * 获取全局序列号
	 * @return
	 * 
	 * @since 1.0.7
	 */
	public String getGlobalSerial(){
		if (globalSerial == null || globalSerial.length() <= 0){
			globalSerial = createGlobalSerial();
		}
		return globalSerial;
	}
	
	/**
	 * 生成全局序列号
	 * @return
	 * 
	 * @since 1.0.7
	 * 
	 */
	protected String createGlobalSerial(){
		return String.valueOf(System.currentTimeMillis()) + KeyGen.getKey(7);
	}
}
