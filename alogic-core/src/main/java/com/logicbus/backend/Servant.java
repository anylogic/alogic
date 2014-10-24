package com.logicbus.backend;

import com.anysoft.pool.Pooled;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.message.*;
import com.logicbus.models.servant.Argument;
import com.logicbus.models.servant.ServiceDescription;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * 服务员(所有服务实现的基类)
 * 
 * @author duanyy
 * 
 * @version 1.0.3 [20140410 duanyy] <br>
 * - 增加调用参数读取的封装函数 <br>
 *     + {@link com.logicbus.backend.Servant#getArgument(String, MessageDoc, Context) getArgument(String,MessageDoc,Context)} <br>
 *     + {@link com.logicbus.backend.Servant#getArgument(String, String, MessageDoc, Context) getArgument(String,String,MessageDoc,Context)} <br>
 * @version 1.0.5 [20140412 duanyy] <br>
 * - 修改消息传递模型。<br>
 * 
 * @version 1.0.8 [20140412 duanyy] <br>
 * - 增加从Message获取参数功能，见{@link com.logicbus.backend.Servant#getArgument(String, Message, Context) getArgument(String, Message, Context)}
 * 和{@link com.logicbus.backend.Servant#getArgument(String, String, Message, Context) getArgument(String, String, Message, Context)}
 * 
 * @version 1.2.4 [20140703 duanyy]<br>
 * - 实现Pooled接口
 * 
 * @version 1.2.5 [20140722 duanyy]<br>
 * - Pooled接口取消了desctory方法，增加了close方法
 */
abstract public class Servant implements Pooled{
	/**
	 * 服务描述
	 */
	protected ServiceDescription m_desc = null;
	
	/**
	 * 服务员的工作状态
	 */
	private int m_state;
	
	/**
	 * 服务调用超时时间
	 */
	private long m_time_out = 3000;
	
	/**
	 * 工作状态:繁忙
	 */
	public static final int STATE_BUSY = 0;
	
	/**
	 * 工作状态：空闲
	 */
	public static final int STATE_IDLE = 1;
	
	/**
	 * 获取服务者的工作状态
	 * @return state
	 */
	public int getState(){return m_state;}
	
	/**
	 * 设置服务者的工作状态
	 * @param state 工作状态
	 */
	public void setState(int state){m_state = state;}
	
	/**
	 * 构造函数
	 */
	public Servant(){
		
	}
	
	/**
	 * a logger of log4j
	 */
	protected static Logger logger = null;
	
	/**
	 * 初始化服务者
	 * 
	 * <br>
	 * 根据服务描述初始化服务者,在{@link com.logicbus.backend.ServantPool ServantPool}类
	 * {@link com.logicbus.backend.ServantPool#CreateServant(ServiceDescription) CreateServant}时调用。
	 * 
	 * @param sd service description
	 * @throws ServantException 
	 */
	public void create(ServiceDescription sd) throws ServantException{
		m_desc = sd;
		
		if (logger == null) {
			logger = LogManager.getLogger(Servant.class.getName());
		}

		m_time_out = PropertiesConstants.getLong(sd.getProperties(), "time_out", 3000);
	}

	/**
	 * 获取超时时长
	 * @return value
	 */
	public long getTimeOutValue(){return m_time_out;}
	
	/**
	 * 判断是否已经超时
	 * @param start_time start time
	 * @return if time out return true,otherwise false.
	 */
	public boolean isTimeOut(long start_time){
		long current = System.currentTimeMillis();
		if (current - start_time > m_time_out) return true;
		return false;
	}
	
	/**
	 * 销毁服务
	 * 
	 * <br>
	 * 在{@link com.logicbus.backend.ServantPool ServantPool}类
	 * {@link com.logicbus.backend.ServantPool#close() close}时调用。
	 * 
	 */
	public void close(){
		
	}

	/**
	 * 获取服务描述
	 * @return 服务描述
	 */
	public ServiceDescription getDescription(){return m_desc;}
	
	/**
	 * 从接口文档和上下文中读取参数
	 * @param id 参数ID
	 * @param defaultValue 缺省值
	 * @param msgDoc 接口文档
	 * @param ctx 上下文
	 * @return 参数值
	 * 
	 * @since 1.0.3
	 */
	public String getArgument(String id,String defaultValue,MessageDoc msgDoc, Context ctx) throws ServantException{
		Argument argu = m_desc.getArgument(id);
		if (argu == null){
			//没有定义参数
			return ctx.GetValue(id, defaultValue);
		}
		
		String value = argu.getValue(msgDoc, ctx);
		if (value == null || value.length() <= 0){
			return defaultValue;
		}
		return value;
	}

	/**
	 * 从Message和上下文中读取参数
	 * @param id 参数ID
	 * @param defaultValue 缺省值
	 * @param msg Message
	 * @param ctx 上下文
	 * @return 参数值
	 * 
	 * @since 1.0.8
	 */
	public String getArgument(String id,String defaultValue,Message msg, Context ctx) throws ServantException{
		Argument argu = m_desc.getArgument(id);
		if (argu == null){
			//没有定义参数
			return ctx.GetValue(id, defaultValue);
		}
		
		String value = argu.getValue(msg, ctx);
		if (value == null || value.length() <= 0){
			return defaultValue;
		}
		return value;
	}	
	
	/**
	 * 从接口文档和上下文中读取参数
	 * @param id 参数ID
	 * @param msgDoc 接口文档
	 * @param ctx 上下文
	 * @return 参数值
	 * 
	 * @since 1.0.3
	 */
	public String getArgument(String id,MessageDoc msgDoc, Context ctx) throws ServantException{
		Argument argu = m_desc.getArgument(id);
		String value = null;
		if (argu == null){
			//没有定义参数
			value = ctx.GetValue(id, "");
		}else{
			value = argu.getValue(msgDoc, ctx);
		}		
		if (value == null || value.length() <= 0){
			throw new ServantException("client.args_not_found",
					"Can not find parameter:" + id);
		}
		return value;
	}	

	/**
	 * 从Message和上下文中读取参数
	 * @param id 参数ID
	 * @param msg Message
	 * @param ctx 上下文
	 * @return 参数值
	 * 
	 * @since 1.0.8
	 */
	public String getArgument(String id,Message msg, Context ctx) throws ServantException{
		Argument argu = m_desc.getArgument(id);
		String value = null;
		if (argu == null){
			//没有定义参数
			value = ctx.GetValue(id, "");
		}else{
			value = argu.getValue(msg, ctx);
		}		
		if (value == null || value.length() <= 0){
			throw new ServantException("client.args_not_found",
					"Can not find parameter:" + id);
		}
		return value;
	}		
	
	/**
	 * 获取参数列表
	 * @return
	 * 
	 * @since 1.0.5
	 */
	public Argument [] getArgumentList(){return m_desc.getArgumentList();}
	
	/**
	 * 服务处理过程
	 * @param msg 消息文档
	 * @param ctx 上下文
	 * @return 
	 * @throws Exception
	 */
	abstract public int actionProcess(MessageDoc msg,Context ctx) throws Exception;
	
	/**
	 * 服务处理即将开始
	 * 
	 * <br>调度框架在{@link #actionProcess(MessageDoc, Context)}之前调用.
	 * @param doc 消息文档
	 * @param ctx 上下文
	 * @see #actionProcess(MessageDoc, Context)
	 */
	public void actionBefore(MessageDoc doc,Context ctx){
		//logger.debug("Begin:" + m_desc.getName());
	}
	
	/**
	 * 服务有效性测试
	 * @param msg 消息文档
	 * @param ctx 上下文
	 * @return 
	 */
	public int actionTesting(MessageDoc msg,Context ctx){
		//logger.debug("Testing the service,I am ok!!!");	
		return 0;
	}
	
	/**
	 * 服务处理已经结束
	 * 
	 * <br>调度框架在{@link #actionProcess(MessageDoc, Context)}之后调用.
	 * 
	 * @param doc 消息文档
	 * @param ctx 上下文
	 * @see #actionProcess(MessageDoc, Context)
	 */
	public void actionAfter(MessageDoc doc,Context ctx){
		doc.setReturn("core.ok","It is successful");
		doc.setEndTime(System.currentTimeMillis());
		//logger.debug("Successful:" + m_desc.getName());
		//logger.debug("Duration(ms):" + (doc.getDuration()));
	}
	
	/**
	 * 服务处理发生异常
	 * 
	 * <br>调度框架在{@link #actionProcess(MessageDoc, Context)}抛出异常时调用
	 * 
	 * @param doc 消息文档
	 * @param ctx 上下文
	 * @see #actionProcess(MessageDoc, Context)
	 * @param ex 
	 */
	public void actionException(MessageDoc doc,Context ctx, ServantException ex){
		doc.setReturn(ex.getCode(), ex.getMessage());
		
		doc.setEndTime(System.currentTimeMillis());
		//logger.debug("Failed:" + m_desc.getName());
		//logger.debug("Duration(ms):" + (doc.getDuration()));		
	}
}
