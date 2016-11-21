package com.logicbus.jms.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.Watcher;
import com.anysoft.util.WatcherHub;
import com.logicbus.jms.JmsModel;
import com.logicbus.jms.JmsModelFactory;

/**
 * 基于Provider的Factory
 * 
 * @author duanyy
 * 
 * @since 1.2.6.1
 *
 * @version 1.2.9.1 [20141017 duanyy]
 * - 淘汰ChangeAware模型，转为更为通用的Watcher模型
 * 
 * @version 1.6.6.5 [20161121 duanyy] <br>
 * - 增加allChanged方法，以便通知Watcher所有对象已经改变
 * 
 */
public class Provided implements JmsModelFactory,Watcher<JmsModel> {
	
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(Provided.class);
	
	/**
	 * the provider to load JmsModel
	 */
	protected JmsModelProvider provider = null;

	/**
	 * a hub to register watcher
	 */
	protected WatcherHub<JmsModel> watcherHub = new WatcherHub<JmsModel>();
	
	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		try {
			TheFactory factory = new TheFactory();
			provider = factory.newInstance(_e, _properties, "provider");
			provider.addWatcher(this);
		}catch (Exception ex){
			logger.error("Can not create provider",ex);
		}
	}

	
	public JmsModel loadModel(String id) {
		return provider != null ? provider.load(id, true) : null;
	}

	
	public void addWatcher(Watcher<JmsModel> watcher) {
		watcherHub.addWatcher(watcher);
	}

	
	public void removeWatcher(Watcher<JmsModel> watcher) {
		watcherHub.removeWatcher(watcher);
	}

	
	public void changed(String id, JmsModel obj) {
		watcherHub.changed(id, obj);
	}
	
	public static class TheFactory extends Factory<JmsModelProvider>{
		
	}

	
	public void added(String id, JmsModel _data) {
		watcherHub.added(id, _data);
	}

	
	public void removed(String id, JmsModel _data) {
		watcherHub.removed(id, _data);
	}


	@Override
	public void allChanged() {
		watcherHub.allChanged();
	}
	
}
