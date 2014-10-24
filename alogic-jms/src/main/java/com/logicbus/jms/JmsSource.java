package com.logicbus.jms;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.Watcher;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;


/**
 * JMS源
 * 
 * @author duanyy
 *
 * @since 1.2.6.1
 * 
 */
public class JmsSource implements Watcher<JmsModel>{
	
	/**
	 * a logger of log4j
	 */
	public static final Logger logger = LogManager.getLogger(JmsSource.class);
	
	/**
	 * 缓存的JMSContext对象
	 */
	protected Hashtable<String,JmsModel> models = new Hashtable<String,JmsModel>();
	
	/**
	 * the JmsModel factories
	 */
	protected List<JmsModelFactory> factories = new ArrayList<JmsModelFactory>();
	
	/**
	 * the only instance 
	 */
	protected static JmsSource instance = null;
	
	/**
	 * 锁
	 */
	protected static Object lock = new Object();
	
	/**
	 * to get the only instance
	 * @return
	 */
	public static JmsSource get(){
		if (instance == null){
			synchronized (lock){
				if (instance == null){
					instance = new JmsSource();
					instance.reload(Settings.get());
				}
			}
		}
		
		return instance;
	}
		
	protected JmsSource(){
		//do noting
	}
	
	public void reload(Properties props){						
		String configFile = props.GetValue("jms.source.master", 
				"java:///com/logicbus/jms/source.xml#com.logicbus.jms.JmsSource");

		String secondaryFile = props.GetValue("jms.source.secondary", 
				"java:///com/logicbus/jms/source.xml#com.logicbus.jms.JmsSource");
		
		Settings profile = Settings.get();
		ResourceFactory rm = (ResourceFactory) profile.get("ResourceFactory");
		if (null == rm){
			rm = new ResourceFactory();
		}
		
		InputStream in = null;
		try {
			in = rm.load(configFile,secondaryFile, null);
			Document doc = XmlTools.loadFromInputStream(in);
			if (doc != null){
				loadConfig(doc.getDocumentElement(),props);
			}
		} catch (Exception ex){
			logger.error("Error occurs when load xml file,source=" + configFile, ex);
		}finally {
			IOTools.closeStream(in);
		}
	}

	private void loadConfig(Element root,Properties props) {		
		TheFactory factory = new TheFactory();
		
		factories.clear();
		
		NodeList children = XmlTools.getNodeListByPath(root, "source");
		for (int i = 0; i < children.getLength() ; i++){
			Node item = children.item(i);
			if (item.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			Element e = (Element)item;			
			try {
				JmsModelFactory f = factory.newInstance(e, props);
				f.addWatcher(this);
				factories.add(f);
			}catch (Exception ex){
				logger.error(ex.getMessage(),ex);
			}
		}
	}
	
	
	/**
	 * 获取指定名称的JMSContext
	 * @param id
	 * @return
	 * @throws JMSException
	 */
	public JmsContext getContext(String id){
		JmsModel found = models.get(id);
		if (found == null){
			synchronized (models){
				found = models.get(id);
				if (found == null){
					for (JmsModelFactory f:factories){
						found = f.loadModel(id);
						if (found != null){
							models.put(id, found);
							break;
						}
					}
				}
			}
		}
		
		return createContext(found);
	}

	/**
	 * 根据model创建JmsContext
	 * 
	 * @param model
	 * @return
	 */
	private JmsContext createContext(JmsModel model){
		if (model == null) return null;
		
		JmsContext instance = null;
		
		String module = model.getModule();
		ClassLoader cl = Settings.getClassLoader();
		try {
			Class<?> clazz = cl.loadClass(module);
			Constructor<?> constructor = clazz.getConstructor(JmsModel.class);
			instance = (JmsContext)constructor.newInstance(model);
		}catch (Throwable t){
			instance = new DefaultJmsContext(model);
			logger.error("Can not create jms context.Using default" 
			+ DefaultJmsContext.class.getName());
		}
		
		return instance;
	}
	
	
	
	public void added(String id, JmsModel _data) {
		// to do nothing
	}

	
	public void removed(String id, JmsModel _data) {
		//从缓存里面清除掉
		models.remove(id);
	}

	
	public void changed(String id, JmsModel _data) {
		models.put(id, _data);		
	}
	
	public static class TheFactory extends Factory<JmsModelFactory>{
		
	}
	
	public static void main(String [] args){
		JmsSource s = JmsSource.get();
		
		JmsContext context = s.getContext("Default");
		if (context == null){
			logger.error("Can not load a jms context named:Default");
			return ;
		}
		
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
