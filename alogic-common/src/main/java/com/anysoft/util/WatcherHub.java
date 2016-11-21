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
 * 
 * @version 1.6.6.5 [20161121 duanyy] <br>
 * - 增加allChanged方法，以便通知Watcher所有对象已经改变
 * 
 */
public class WatcherHub<data> implements Watcher<data> {

	@Override
	public void added(String id, data _data) {
		for (Watcher<data> w:listeners){
			if (w != null){
				w.added(id, _data);
			}
		}
	}

	@Override
	public void removed(String id, data _data) {
		for (Watcher<data> w:listeners){
			if (w != null){
				w.removed(id, _data);
			}
		}
	}

	@Override
	public void changed(String id, data _data) {
		for (Watcher<data> w:listeners){
			if (w != null){
				w.changed(id, _data);
			}
		}		
	}
	
	@Override
	public void allChanged() {
		for (Watcher<data> w:listeners){
			if (w != null){
				w.allChanged();
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
