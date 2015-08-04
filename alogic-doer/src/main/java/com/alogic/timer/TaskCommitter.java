package com.alogic.timer;

/**
 * 任务提交者
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public interface TaskCommitter {
	
	/**
	 * 提交任务
	 * 
	 * @param task 待提交的任务
	 * @param timer 触发任务的timer
	 * 
	 */
	public void commit(Task task,Timer timer);
}
