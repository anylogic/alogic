package com.logicbus.backend.message;

import java.sql.Connection;
import com.anysoft.util.DefaultProperties;
import com.logicbus.backend.ServantException;

/**
 * 消息文档
 * @author duanyy
 * 
 * @version 1.0.4 [20140410 duanyy] <br>
 * - 增加对RawMessage的支持<br>
 * 
 * @version 1.0.5 [20140412 duanyy] <br>
 * - 修改消息传递模型。<br>
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 将MessageDoc和Context进行合并整合 <br>
 */
abstract public class MessageDoc extends DefaultProperties{

	/**
	 * 文档编码
	 */
	protected String encoding = "utf-8";
	
	/**
	 * 获取文档编码
	 * @return 编码
	 */
	public String getEncoding(){return encoding;}
	
	/**
	 * 构造上下文
	 * 
	 * @param _encoding
	 */
	protected MessageDoc(String _encoding){
		encoding = _encoding;
	}
		
	/**
	 * a db connection
	 */
	private Connection m_conn;
	
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
	 * 结果代码
	 */
	protected String returnCode = "core.ok";
	
	/**
	 * 原因
	 */
	protected String reason = "It is ok.";
	
	/**
	 * 时长
	 */
	protected long duration = 0;

	/**
	 * the start time
	 */
	private long m_start_time;
	/**
	 * the end time
	 */
	private long m_end_time;
	
	/**
	 * to get the start time
	 * @return start time
	 */
	public long getStartTime(){return m_start_time;}
	
	/**
	 * to set the start time
	 * @param start_time start time
	 */
	public void setStartTime(long start_time){m_start_time = start_time;}
	
	/**
	 * to get the end time
	 * @return end time
	 */
	public long getEndTime(){return m_end_time;}

	/**
	 * to set the end time
	 * @param end_time end time
	 */
	public void setEndTime(long end_time){m_end_time = end_time;}	
	
	/**
	 * 获取结果代码
	 * @return 结果代码
	 */
	public String getReturnCode(){return returnCode;}
	
	/**
	 * 获取原因
	 * @return 原因
	 */
	public String getReason(){return reason;}
	
	/**
	 * 获取时长
	 * @return 时长
	 */
	public long getDuration(){return m_end_time - m_start_time;}
	
	/**
	 * 设置调用结果
	 * 
	 * @param _code 结果代码
	 * @param _reason 原因
	 */	
	public void setReturn(String _code,String _reason){
		returnCode = _code;
		reason = _reason;
	}
	
	/**
	 * 消息实例
	 */
	protected Message msg = null;
	
	/**
	 * 作为消息处理
	 * 
	 * @param clazz Message实现类
	 * @throws ServantException 当创建Message实例发生异常的时候，抛出异常代码为:core.instance_create_error
	 */
	public <message extends Message> Message asMessage(Class<message> clazz) throws ServantException{
		if (msg != null)
			return msg;
		try {
			msg = (Message)clazz.newInstance();
			onMessageCreated(msg);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServantException("core.instance_create_error",
					"Can not create instance of " + clazz.getName() + ":" + e.getMessage());
		}
		return msg;
	}
	
	/**
	 * MessageCreated事件处理
	 * @param msg 消息
	 */
	protected void onMessageCreated(Message msg) {
		
	}

	/**
	 * 完成服务，写出结果
	 */
	abstract public void finish();
	
	/**
	 * to get the client ip
	 * @return client ip
	 */
	abstract public String getClientIp();
	
	/**
	 * 获取主机信息
	 * @return 主机信息
	 */
	abstract public String getHost();
	
	/**
	 * 获取请求路径
	 * @return request路径
	 */
	abstract public String getRequestURI();
	
	/**
	 * 获取全局序列号
	 * @return 全局序列号
	 * 
	 * @since 1.0.7
	 */
	abstract public String getGlobalSerial();
	
	abstract public String getReqestContentType();
	
	abstract public String getRequestHeader(String id);
	
	abstract public void setResponseHeader(String id,String value);

}
