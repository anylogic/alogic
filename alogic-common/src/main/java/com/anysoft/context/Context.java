package com.anysoft.context;

import com.anysoft.util.Reportable;
import com.anysoft.util.Watcher;
import com.anysoft.util.XMLConfigurable;


/**
 * 通用配置环境
 * 
 * @author duanyy
 *
 * @param <object> 配置对象
 * 
 * @since 1.5.0
 * 
 * @version 1.5.2 [20141017 duanyy]
 * - 实现Reportable接口
 */
public interface Context<object extends Reportable> extends XMLConfigurable, AutoCloseable,Reportable {
	
	/**
	 * 通过ID获取对象
	 * 
	 * @param id
	 * @return
	 */
	 
	public object get(String id);
	
	/**
	 * 注册监控器
	 * @param watcher
	 */
	public void addWatcher(Watcher<object> watcher);
	
	/**
	 * 注销监控器
	 * @param watcher
	 */
	public void removeWatcher(Watcher<object> watcher);
}
