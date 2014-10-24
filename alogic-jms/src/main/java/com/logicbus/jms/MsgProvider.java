package com.logicbus.jms;

import javax.jms.Message;
import javax.jms.Session;


/**
 * 消息提供者
 * 
 * @author duanyy
 *
 */
public interface MsgProvider {
	
	/**
	 * 生成消息
	 * @param session JMS会话
	 * @return
	 * @throws Exception
	 */
	public Message [] message(Session session)throws Exception;
}
