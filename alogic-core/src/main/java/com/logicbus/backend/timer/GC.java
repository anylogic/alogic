package com.logicbus.backend.timer;

import com.anysoft.util.Properties;

/**
 * 定时任务（定期释放内存）
 * @author duanyy
 *
 */
public class GC extends Task {

	public Object createContext(Properties _config) {
		return null;
	}

	public void run(Object _context, Properties _config, TaskListener _listener) {
		if (_listener != null){
			_listener.taskMessage(this, "GC start...");
		}
		System.gc();
		if (_listener != null){
			_listener.taskMessage(this, "GC end.");
		}
	}
}
