package com.anysoft.cache;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.Watcher;
import com.anysoft.util.WatcherHub;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * DataStore的虚类
 * 
 * @author duanyy
 *
 * @param <data>
 * 
 * @version 1.5.2 [20141017 duanyy]
 * - 淘汰ChangeAware机制，采用更为通用的Watcher
 * 
 */
abstract public class AbstractDataStore<data extends Cacheable> implements DataStore<data>,XMLConfigurable {

	
	public data load(String id) {
		return load(id,true);
	}

	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties props = new XmlElementProperties(_e,_properties);
		create(props);
		onConfigure(_e,props);
	}

	/**
	 * configure时间处理
	 * @param _e
	 * @param props
	 */
	protected void onConfigure(Element _e, XmlElementProperties props) {
		
	}

	
	public void addWatcher(Watcher<data> watcher) {
		if (watchers != null)
			watchers.addWatcher(watcher);
	}

	
	public void removeWatcher(Watcher<data> watcher) {
		if (watchers != null)
			watchers.removeWatcher(watcher);
	}
	
	protected WatcherHub<data> watchers = new WatcherHub<data>();
}
