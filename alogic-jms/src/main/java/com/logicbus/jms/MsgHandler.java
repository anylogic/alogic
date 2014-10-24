package com.logicbus.jms;
import javax.jms.Message;


/**
 * 消息处理器
 * 
 * @author duanyy
 *
 */
public interface MsgHandler {
	/**
	 * 处理消息
	 * @param msg JMS消息
	 * @throws Exception
	 */
	public void message(Message msg) throws Exception;
}
