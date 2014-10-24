package com.anysoft.util;

import java.util.ArrayList;
import java.util.List;

/**
 * WatcherHub
 * 
 * <br>
 * 用于保存多个监听器，并分发消息到多个监听器
 * 
 * @author duanyy
 *
 * @param <data>
 */
public class WatcherHub<data> implements Watcher<data> {

	
	public void added(String id, data _data) {
		for (Watcher<data> w:listeners){
			if (w != null){
				w.added(id, _data);
			}
		}
	}

	
	public void removed(String id, data _data) {
		for (Watcher<data> w:listeners){
			if (w != null){
				w.removed(id, _data);
			}
		}
	}

	
	public void changed(String id, data _data) {
		for (Watcher<data> w:listeners){
			if (w != null){
				w.changed(id, _data);
			}
		}		
	}

	/**
	 * 注册监听器
	 * @param watcher
	 */
	public void addWatcher(Watcher<data> watcher){
		listeners.add(watcher);
	}
	
	/**
	 * 注销监听器
	 * @param watcher
	 */
	public void removeWatcher(Watcher<data> watcher){
		listeners.remove(watcher);
	}
	
	protected List<Watcher<data>> listeners = new ArrayList<Watcher<data>>();
}
