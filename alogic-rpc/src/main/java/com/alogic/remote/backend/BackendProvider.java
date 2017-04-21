package com.alogic.remote.backend;

import com.anysoft.util.Configurable;
import com.anysoft.util.Reportable;
import com.anysoft.util.Watcher;
import com.anysoft.util.XMLConfigurable;

/**
 * 后端提供者
 * @author yyduan
 *
 */
public interface BackendProvider extends Reportable,XMLConfigurable,Configurable{
	
	/**
	 * 装入指定应用的后端列表
	 * 
	 * @param appId
	 * @return
	 */
	public AppBackends load(String appId);
	
	/**
	 * 增加监听器
	 * @param watcher 监听器
	 */
	public void addWatcher(Watcher<AppBackends> watcher);
	
	/**
	 * 移除监听器 
	 * @param watcher 监听器
	 */
	public void removeWatcher(Watcher<AppBackends> watcher);
}
