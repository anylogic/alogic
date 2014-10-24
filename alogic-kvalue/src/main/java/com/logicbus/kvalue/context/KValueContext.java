package com.logicbus.kvalue.context;


import com.anysoft.util.Reportable;
import com.anysoft.util.Watcher;
import com.anysoft.util.XMLConfigurable;
import com.logicbus.kvalue.core.Schema;

/**
 * KVDB上下文
 * 
 * @author duanyy
 *
 */
public interface KValueContext extends AutoCloseable,XMLConfigurable,Reportable{
	/**
	 * 查找指定的Schema
	 * 
	 * @param id
	 * @return
	 */
	public Schema getSchema(String id);
	
	/**
	 * 注册监听器
	 * @param watcher
	 */
	public void addWatcher(Watcher<Schema> watcher);
	
	/**
	 * 注销监听器
	 * @param watcher
	 */
	public void removeWatcher(Watcher<Schema> watcher);
}
