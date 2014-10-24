package com.logicbus.jms;

import javax.jms.JMSException;


/**
 * JMS环境
 * 
 * @author duanyy
 *
 */
public interface JmsContext{
	
	/**
	 * 打开
	 * @throws JMSException
	 */
	public void open() throws JMSException;

	/**
	 * 关闭上下文
	 */
	public void close();
	
	/**
	 * 
	 * 获取指定的Destination
	 * 
	 * @param id id
	 * @return
	 * @throws JMSException
	 */
	public JmsDestination getDestination(String id) throws JMSException;

}
