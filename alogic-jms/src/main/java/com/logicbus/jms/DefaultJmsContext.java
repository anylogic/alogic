package com.logicbus.jms;

import java.lang.reflect.Constructor;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import com.anysoft.cache.SimpleModel;
import com.anysoft.util.Confirmer;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;

public class DefaultJmsContext implements JmsContext {
	
	public DefaultJmsContext(JmsModel _model){
		model = _model;
	}
	
	private JmsModel model = null;
	protected Connection conn = null;
	protected Session session = null;
	
	public void open() throws JMSException {		 		
        // 构造从工厂得到连接对象
		conn = getConnection(model);
	        // 启动
		conn.start();
		
		session = conn.createSession(isTransacted(), getAcknowledgeMode());
	}


	/**
	 * 根据配置信息创建JMS连接
	 * @param _model 配置信息
	 * @return
	 * @throws JMSException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Connection getConnection(JmsModel _model) throws JMSException{
		String brokerURI = PropertiesConstants.getString(_model, "brokerURI", "");
		String username = PropertiesConstants.getString(_model, "username","");
		String password = PropertiesConstants.getString(_model,"password","");
		
		String callbackId = PropertiesConstants.getString(_model, "callbackId","");
		String callback = PropertiesConstants.getString(_model,"callback","");
		
		ClassLoader cl = getClassLoader();
		
		if (callbackId != null && callbackId.length() > 0 && callback != null && callback.length() > 0){
			Confirmer confirmer = null;
			
			try {
				confirmer = (Confirmer)cl.loadClass(callback).newInstance();
				confirmer.prepare(callbackId);
			}catch (Exception ex){
				confirmer = null;
			}
			
			if (confirmer != null){
				brokerURI = confirmer.confirm("brokerURI",brokerURI);
				username = confirmer.confirm("username",username);
				password = confirmer.confirm("password",password);
			}
		}
		
		if (brokerURI == null || brokerURI.length() <= 0){
			throw new JMSException("Can not found an available broker uri.");
		}
		
		String factoryClass = PropertiesConstants.getString(_model, "jmsImpl", "org.apache.activemq.ActiveMQConnectionFactory");
		
		ConnectionFactory connectionFactory = null;
		try {
			Class clazz = cl.loadClass(factoryClass);
			Constructor constructor = clazz.getConstructor(new Class[]{String.class});
			connectionFactory = (ConnectionFactory) constructor.newInstance(new Object[]{brokerURI});
		}catch (Exception ex){
			throw new JMSException("Can not create instance of jms impl,class = " + factoryClass);
		}
			
		return connectionFactory.createConnection(username,password);
	}
	
	
	public JmsDestination getDestination(String id) throws JMSException{
		SimpleModel destModel = model.getDestination(id);
		
		Destination destination = null;
		
		if (destModel == null){
			boolean idAsQueueName = PropertiesConstants.getBoolean(model, "idAsQueueName", Boolean.FALSE);
			if (idAsQueueName){
				destModel = model;
			}
		}
		
		if (destModel == null)
			throw new JMSException("Can not create a destination : " + id);

		// 类型是topic还是queue，缺省为queue
		String type = PropertiesConstants.getString(destModel, "type", "queue");
		// destination的名称，缺省为id
		String name = PropertiesConstants.getString(destModel, "name", id);

		if ("queue".equals(type)) {
			destination = session.createQueue(name);
		} else {
			destination = session.createTopic(name);
		}

		return new JmsDestination(session, destination, destModel);
	}
	
	
	public void close(){
		if (session != null){
			try {
				session.close();
			} catch (JMSException e) {
			}
		}
		if (conn != null){
			try {
				conn.close();
			} catch (JMSException e) {
			}
		}
	}
	/**
	 * 获取当前的ClassLoader
	 * @return
	 */
	protected ClassLoader getClassLoader(){
		Settings settings = Settings.get();
		
		ClassLoader cl = (ClassLoader) settings.get("classLoader");
		if (cl == null){
			cl = Thread.currentThread().getContextClassLoader();
		}
		
		return cl;
	}

	
	protected boolean isTransacted(){
		return PropertiesConstants.getBoolean(model, "transacted", true);
	}
    
	protected int getAcknowledgeMode() {
		String acknowledgeMode  = PropertiesConstants.getString(model, "acknowledgeMode", "AUTO").toUpperCase();
		
		if (acknowledgeMode.equals("AUTO")){
			return Session.AUTO_ACKNOWLEDGE;
		}
		
		if (acknowledgeMode.equals("CLIENT")){
			return Session.CLIENT_ACKNOWLEDGE;
		}
		
		if (acknowledgeMode.equals("TRANSACTED")){
			return Session.SESSION_TRANSACTED;
		}
		
		if (acknowledgeMode.equals("DUPS_OK")){
			return Session.DUPS_OK_ACKNOWLEDGE;
		}
		return Session.AUTO_ACKNOWLEDGE;
	}
	
	public static void main(String [] args){
		
		JmsModel model = new JmsModel("Default");
		
		model.SetValue("brokerURI", "tcp://localhost:61616");
		model.SetValue("idAsQueueName", "true");
		model.SetValue("transacted", "false");
		
		DefaultJmsContext context = new DefaultJmsContext(model);
		
		
		try {
			context.open();			
			
			JmsDestination queue = context.getDestination("Default");			
			queue.send(new MsgProvider(){

				
				public Message[] message(Session session)
						throws Exception {
					Message [] msgs = new Message[5];
					for (int i = 0 ; i < 5 ; i ++){
						Message msg = session.createTextMessage("Helloworld " + i);
						msgs[i] = msg;
					}
					return msgs;
				}
				
			});					
		}catch (Exception ex){
			ex.printStackTrace();
		}finally{
			context.close();
		}
		
		
		try {
			context.open();
			
			JmsDestination queue = context.getDestination("Default");
			
			queue.receive(new MsgHandler(){

				
				public void message(Message msg) throws Exception {
					System.out.println(msg);
				}
				
			},5000);
						
		}catch (Exception ex){
			ex.printStackTrace();
		}finally{
			context.close();
		}	
	}	
}
