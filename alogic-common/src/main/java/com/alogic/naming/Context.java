package com.alogic.naming;

import com.anysoft.util.Configurable;
import com.anysoft.util.Reportable;
import com.anysoft.util.Watcher;
import com.anysoft.util.XMLConfigurable;

/**
 * 通用配置环境
 * 
 * @author duanyy
 *
 * @since 1.6.6.8
 */
public interface Context<O extends Reportable> extends XMLConfigurable,AutoCloseable,Reportable,Configurable {
	
	/**
	 * 通过全局名称来查找对象
	 * 
	 * @param name 对象名称或id
	 * @return 对象
	 */
	public O lookup(String name);
	
	/**
	 * 注册监控器
	 * @param watcher 监控器
	 */
	public void addWatcher(Watcher<O> watcher);
	
	/**
	 * 注销监控器
	 * @param watcher 监控器
	 */
	public void removeWatcher(Watcher<O> watcher);	
}
