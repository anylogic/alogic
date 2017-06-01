package com.alogic.doer.core;

import com.alogic.timer.core.Task;
import com.anysoft.util.Configurable;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * 任务分发器
 * 
 * @author duanyy
 * @since 1.6.3.4
 * @version 1.6.9.2 [20170601 duanyy] <br>
 * - 改造TaskCenter模型，以便提供分布式任务处理支持; <br>
 */
public interface TaskDispatcher extends XMLConfigurable,Configurable, Reportable {
	/**
	 * 分发任务
	 * 
	 * @param queue 队列
	 * @param task 任务实例
	 * 
	 */
	public void dispatch(String queue,Task task);
}
