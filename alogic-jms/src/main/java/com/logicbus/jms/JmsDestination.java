package com.logicbus.jms;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import com.anysoft.cache.SimpleModel;
import com.anysoft.util.PropertiesConstants;

public class JmsDestination {
	protected Session session = null;
	protected Destination destination = null;
	protected SimpleModel model = null; 
	
	public JmsDestination(Session _session, Destination _destination, SimpleModel destModel) {
		destination = _destination;
		session = _session;
		model = destModel;
	}

	/**
	 * 发送消息
	 * @param sender 消息提供者
	 * @throws Exception
	 */
	public void send(MsgProvider sender) throws Exception{
		if (sender == null){
			return ;
		}
		MessageProducer producer = null;
		try {
			producer = session.createProducer(destination);		
			
			producer.setDeliveryMode(getDeliveryMode());
			producer.setPriority(getPriority());
			producer.setTimeToLive(getTTL());
			
			Message [] msgs = sender.message(session);
			
			for (Message msg:msgs){
				producer.send(msg);
			}
			
			if (session.getTransacted())
				session.commit();
		}catch (Exception ex){
			if (session.getTransacted())
				session.rollback();
			throw ex;
		}finally{
			if (producer != null){
				producer.close();
			}
		}
	}
	
	/**
	 * 接受消息
	 * 
	 * @param receiver 消息接收器
	 * @param timeout 超时时间
	 * @throws Exception
	 */
	public void receive(MsgHandler receiver,long timeout) throws Exception{
		if (receiver == null){
			return ;
		}
		
		MessageConsumer consumer = null;
		
		try {
			consumer = getMessageConsumer();
					
			while (true) {
				Message message = consumer.receive(timeout);
	            if (null != message) {
	                	receiver.message(message);
	            } else {
	                break;
	            }
	        }
		}finally{
			if (consumer != null)
				consumer.close();
		}
	}
	
	/**
	 * 直接获取MessageConsumer
	 * 
	 * <br>
	 * 通常用于异步消息接收模式。
	 * 
	 * @return
	 * @throws Exception
	 */
	public MessageConsumer getMessageConsumer() throws Exception{
		return (destination instanceof Topic && isDurable()) ? 
				session.createDurableSubscriber((Topic)destination, getSubscriberName()) :session.createConsumer(destination);
	}
	
	protected int getDeliveryMode(){
		String mode = PropertiesConstants.getString(model, "deliveryMode", "NON_PERSISTENT");
		return mode.toUpperCase().equals("PERSISTENT") ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT;
	}
	
	protected boolean isDurable(){
		return PropertiesConstants.getBoolean(model, "isDurable", Boolean.FALSE);
	}
	
	protected int getPriority(){
		return PropertiesConstants.getInt(model, "priority", 4);
	}
	
	protected long getTTL(){
		return PropertiesConstants.getLong(model, "timeToLive", 0);
	}
	
	protected String getSubscriberName(){
		return PropertiesConstants.getString(model, "subscriber", "subscriber");
	}
}
