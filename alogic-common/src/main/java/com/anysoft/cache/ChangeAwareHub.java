package com.anysoft.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * ChangeAwareHub
 * 
 * @author duanyy
 *
 * @param <data>
 * 
 * @version 1.5.2 [20141017 duanyy]
 * - 淘汰ChangeAware机制，采用更为通用的Watcher
 * 
 * @deprecated
 */
public class ChangeAwareHub<data extends Cacheable> implements ChangeAware<data> {

	
	public void changed(String id, data obj) {
		for (ChangeAware<data> listener:listeners){
			if (listener != null){
				listener.changed(id, obj);
			}
		}
	}

	/**
	 * 清除所有注册的Listener
	 */
	public void clear(){
		listeners.clear();
	}
	
	/**
	 * 注册Listener
	 * 
	 * @param listener
	 */
	public void add(ChangeAware<data> listener){
		listeners.add(listener);
	}

	public void remove(ChangeAware<data> listener){
		listeners.remove(listener);
	}
	
	protected List<ChangeAware<data>> listeners = new ArrayList<ChangeAware<data>>();
}
