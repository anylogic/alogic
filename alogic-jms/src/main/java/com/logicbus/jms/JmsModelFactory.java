package com.logicbus.jms;

import com.anysoft.util.Watcher;
import com.anysoft.util.XMLConfigurable;


/**
 * JmsModelFactory
 * 
 * <br>
 * 用于装入JmsModel
 * 
 * @author duanyy
 *
 */
public interface JmsModelFactory extends XMLConfigurable{
	
	/**
	 * 装入Model
	 * @param id
	 * @return
	 */
	public JmsModel loadModel(String id);
	
	/**
	 * 注册监听器
	 * @param watcher
	 */
	public void addWatcher(Watcher<JmsModel> watcher);
	
	/**
	 * 注销监听器
	 * @param watcher
	 */
	public void removeWatcher(Watcher<JmsModel> watcher);
}
